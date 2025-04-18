import assert from "assert";
import microtime from "microtime";
export interface RateLimiterOptions {
  interval: number;
  maxInInterval: number;
  minDifference: number;
  client: any;
}

export interface RateLimitInfo {
  blocked: boolean;
  blockedDueToCount: boolean;
  blockedDueToMinDifference: boolean;
  millisecondsUntilAllowed: number;
  actionsRemaining: number;
}

// interface RedisClientWrapper {
//   del(arg: string): unknown;
//   multi(): RedisMultiWrapper;
//   parseZRangeResult(result: unknown): Array<number>;
//   zRemRangeByScore(key: string, min: number, max: number): void;
//   zAdd(key: string, score: number, value: string): void;
//   zRangeWithScores(key: string, min: number, max: number): void;
//   expire(key: string, time: number): void;
//   exec(): Promise<Array<unknown>>;
// }

// interface RedisMultiWrapper {
//   zRemRangeByScore(key: string, min: number, max: number): void;
//   zAdd(key: string, score: number, value: string): void;
//   zRangeWithScores(key: string, min: number, max: number): void;
//   expire(key: string, time: number): void;
//   exec(): Promise<Array<unknown>>;
// }

export class RateLimiter {
  private readonly interval: number; // Rate Limit을 검사할 시간 간격
  private readonly maxInInterval: number; // 시간 간격 내에서 허용할 최대 요청 수
  private readonly minDifference: number; // 요청 간격의 최소 차이
  private readonly client: any; // Redis 클라이언트
  private readonly ttl: number; // Redis에 저장된 데이터의 수명

  constructor(options: RateLimiterOptions) {
    assert(options.interval > 0, "interval은 0보다 크게 설정해야 합니다.");
    assert(
      options.maxInInterval > 0,
      "maxInInterval은 0보다 크게 설정해야 합니다."
    );
    assert(
      options.minDifference >= 0,
      "minDifference은 0보다 크거나 같게 설정해야 합니다."
    );

    this.interval = millisecondsToMicroseconds(options.interval); // 시간 간격을 microseconds로 변환
    this.maxInInterval = options.maxInInterval;
    this.minDifference = millisecondsToMicroseconds(options.minDifference);
    this.client = options.client;
    this.ttl = this.interval; //
  }

  async limitWithInfo(id: string): Promise<RateLimitInfo> {
    const timestamps = await this.getTimestamps(id, true);
    return this.calculateInfo(timestamps);
  }

  private makeKey(id: string) {
    return `rate-limiter:${id}`;
  }

  private async getTimestamps(id: string, addNewTimestamp: boolean) {
    const now = getCurrentMicroseconds();
    if (!now) {
      throw new Error("now is undefined");
    }
    const key = this.makeKey(id);

    // 현재 시간에서 interverl 시간 만큼을 빼서 삭제할 데이터의 시간 범위를 설정
    const clearBefore = now - this.interval;

    // Redis transaction 처리
    const batch = this.client.multi();

    // interval 시간 전의 데이터를 삭제
    batch.zRemRangeByScore(key, 0, clearBefore);

    // 새로운 요청이 있을 경우 현재 시간을 추가
    if (addNewTimestamp) {
      batch.zAdd(key, [{ score: now, value: now.toString() }]);
    }

    // 전체 데이터를 조회
    batch.zRangeWithScores(key, 0, -1);

    // ttl은 interval과 같음, interval이 지나면 로그 데이터의 검증이 필요 없으므로 삭제 처리
    batch.expire(key, this.ttl);

    const results = await batch.exec();

    // 새로운 요청 시간을 추가한 경우, 2번째 인덱스에 저장된 요청 시간을 가져옴
    // 새로운 요청 시간을 추가하지 않은 경우, 1번째 인덱스에 저장된 요청 시간을 가져옴
    const zRangeResult = addNewTimestamp ? results[2] : results[1];

    return this.parseZRangeResult(zRangeResult);
  }

  private parseZRangeResult(zRangeResult: any) {
    return (
      zRangeResult as Array<{
        value: string;
        score: number;
      }>
    ).map(({ score }) => score);
  }

  private async calculateInfo(timestamps: Array<number>) {
    // 전달된 timestamps 배열의 길이
    const numTimestamps = timestamps.length;
    // 가장 최근의 요청 시간
    const currentTimestamp = timestamps[numTimestamps - 1];
    // 가장 최근의 요청 시간 이전의 요청 시간
    const previousTimestamp = timestamps[numTimestamps - 2];

    // 요청 수가 최대 요청 수를 초과한 경우
    const blockedDueToCount = numTimestamps > this.maxInInterval;

    // 요청 간격의 최소 차이를 초과한 경우
    const blockedDueToMinDifference =
      previousTimestamp !== null &&
      currentTimestamp - previousTimestamp < this.minDifference;

    // 요청 수가 최대 요청 수를 초과한 경우 또는 요청 간격의 최소 차이를 초과한 경우
    const blocked = blockedDueToCount || blockedDueToMinDifference;

    /**
     * Rate Limiter에서 요청이 언제 다시 허용될 수 있는지 계산
     *
     * @example
     * maxInInterval = 5
     * interval = 1000ms
     * 현재 요청 시간들 = [100, 200, 300, 400, 500, 600, 700]
     *
     * 1. 가장 오래된 요청 시간 = 300 (2번째 요청)
     * 2. 현재 시간 + 인터벌 = 700 + 1000 = 1700
     * 3. 남은 시간 = 300 - 1700 = -1400
     * 4. 결과: 1400ms 후에 요청 가능
     */

    const microsecondsUntilUnblocked =
      numTimestamps >= this.maxInInterval
        ? timestamps[Math.max(0, numTimestamps - this.maxInInterval)] -
          (currentTimestamp + this.interval)
        : 0;

    /**
     * @description
     * 1. minDifference: 연속된 요청 사이의 최소 시간 간격
     * 2. microsecondsUntilUnblocked: 최대 요청 수 제한으로 인한 대기 시간
     *
     * 두 개의 제한 중 더 큰 값이 적용되어야 하므로 Max 사용
     */
    const microsecondsUntilAllowed = Math.max(
      this.minDifference,
      microsecondsUntilUnblocked
    );

    return {
      blocked,
      blockedDueToCount,
      blockedDueToMinDifference,
      millisecondsUntilAllowed: microsecondsToMilliseconds(
        microsecondsUntilAllowed
      ),
      // 현재의 Interval 내에서 남은 요청 수
      actionsRemaining: Math.max(0, this.maxInInterval - numTimestamps),
    };
  }
}

/**
 * milliseconds를 microseconds로 변환
 * @param milliseconds - 변환할 milliseconds
 * @returns 변환된 microseconds
 */
export function millisecondsToMicroseconds(milliseconds: number) {
  return 1000 * milliseconds;
}

export function getCurrentMicroseconds() {
  return microtime.now();
}

export function microsecondsToMilliseconds(microseconds: number) {
  return microseconds / 1000;
}
