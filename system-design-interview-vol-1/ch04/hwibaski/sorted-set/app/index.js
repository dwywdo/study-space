const express = require("express");
const { createClient } = require("redis");

const app = express();
const port = 3000;
app.use(express.json()); // JSON 요청 처리
app.use(express.urlencoded({ extended: true })); // form-data 처리

// Redis 클라이언트 생성
// url: Redis 서버의 연결 정보 (docker-compose에서 정의한 서비스 이름 'redis'를 hostname으로 사용)
const client = createClient({
  url: "redis://redis:6379",
});

client.on("error", (err) => console.log("Redis Client Error", err));

// 초기 데이터 설정
// Redis Sorted Set은 score(점수)와 value(값)의 쌍으로 구성된 데이터 구조
// score는 double 타입의 부동소수점 숫자로, 이 값을 기준으로 자동 정렬됨
// value는 문자열로 저장되며, score가 같을 경우 value의 사전순으로 정렬됨
async function initializeData() {
  try {
    await client.connect();
    console.log("Redis에 연결되었습니다.");

    // 기존 데이터가 없을 때만 초기 데이터 추가
    // EXISTS: 키가 존재하는지 확인하는 Redis 명령어
    const exists = await client.exists("leaderboard");
    if (!exists) {
      // zAdd: Sorted Set에 멤버 추가
      // score와 value의 쌍으로 데이터 저장
      // score는 정렬의 기준이 되며, value는 고유한 식별자
      await client.zAdd("leaderboard", [
        { score: 100, value: "user1" },
        { score: 200, value: "user2" },
        { score: 150, value: "user3" },
        { score: 300, value: "user4" },
      ]);
      console.log("초기 데이터가 추가되었습니다.");
    }
  } catch (error) {
    console.error("데이터 초기화 중 에러:", error);
  }
}

// HTML 템플릿
const htmlTemplate = (data) => `
<!DOCTYPE html>
<html>
<head>
  <title>Redis Sorted Set 테스트</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    table { border-collapse: collapse; width: 100%; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    .container { max-width: 800px; margin: 0 auto; }
  </style>
</head>
<body>
  <div class="container">
    <h1>Redis Sorted Set 데이터</h1>
    <h2>리더보드 (오름차순)</h2>
    <table>
      <tr>
        <th>순위</th>
        <th>사용자</th>
        <th>점수</th>
      </tr>
      ${data.ascending
        .map(
          (member, index) => `
        <tr>
          <td>${index + 1}</td>
          <td>${member.value}</td>
          <td>${member.score}</td>
        </tr>
      `
        )
        .join("")}
    </table>

    <h2>리더보드 (내림차순)</h2>
    <table>
      <tr>
        <th>순위</th>
        <th>사용자</th>
        <th>점수</th>
      </tr>
      ${data.descending
        .map(
          (member, index) => `
        <tr>
          <td>${index + 1}</td>
          <td>${member.value}</td>
          <td>${member.score}</td>
        </tr>
      `
        )
        .join("")}
    </table>

    <h2>150-250 점수 범위</h2>
    <table>
      <tr>
        <th>순위</th>
        <th>사용자</th>
        <th>점수</th>
      </tr>
      ${data.range
        .map(
          (member, index) => `
        <tr>
          <td>${index + 1}</td>
          <td>${member.value}</td>
          <td>${member.score}</td>
        </tr>
      `
        )
        .join("")}
    </table>
  </div>
</body>
</html>
`;

// 라우트 핸들러
app.get("/", async (req, res) => {
  try {
    if (!client.isOpen) {
      await client.connect();
    }

    // ZRANGE: Sorted Set의 멤버를 조회
    // 0(start), -1(end like python): 전체 범위 조회
    // WITHSCORES: score 값도 함께 반환
    const ascendingRaw = await client.zRangeWithScores("leaderboard", 0, -1);
    const ascending = [];
    for (let i = 0; i < ascendingRaw.length; i += 2) {
      ascending.push({
        value: ascendingRaw[i].value,
        score: ascendingRaw[i].score,
      });
    }

    // REV: true 옵션으로 내림차순 정렬
    const descendingRaw = await client.zRangeWithScores("leaderboard", 0, -1, {
      REV: true,
    });
    const descending = [];
    for (let i = 0; i < descendingRaw.length; i += 2) {
      descending.push({
        value: descendingRaw[i].value,
        score: descendingRaw[i].score,
      });
    }

    // ZRANGEBYSCORE: 특정 점수 범위의 멤버 조회
    // 150, 250: 최소 점수와 최대 점수 지정
    const rangeRaw = await client.zRangeByScoreWithScores(
      "leaderboard",
      150,
      250
    );
    const range = [];
    for (let i = 0; i < rangeRaw.length; i += 2) {
      range.push({
        value: rangeRaw[i].value,
        score: rangeRaw[i].score,
      });
    }

    res.send(htmlTemplate({ ascending, descending, range }));
  } catch (error) {
    console.error("에러 발생:", error);
    res.status(500).send("서버 에러가 발생했습니다.");
  }
});

// 직접 score 설정
app.post("/update-score", async (req, res, next) => {
  try {
    console.log(req.body);
    const { member, score } = req.body;
    await client.zAdd("leaderboard", [
      {
        score: parseInt(score),
        value: member,
      },
    ]);
    res.json({ success: true });
  } catch (error) {
    next(error); // 에러를 전역 에러 핸들러로 전달
  }
});

// score 증가/감소
app.post("/increment-score", async (req, res, next) => {
  try {
    console.log(req.body);
    const { member, increment } = req.body;
    const newScore = await client.zIncrBy(
      "leaderboard",
      parseInt(increment),
      member
    );
    res.json({ success: true, newScore });
  } catch (error) {
    next(error); // 에러를 전역 에러 핸들러로 전달
  }
});

// 404 핸들러 (존재하지 않는 라우트 처리)
app.use((req, res, next) => {
  res.status(404).json({
    error: "Not Found",
    message: "요청하신 리소스를 찾을 수 없습니다.",
  });
});

// 전역 에러 핸들러 (모든 라우트와 미들웨어 이후에 정의)
app.use((err, req, res, next) => {
  console.error("전역 에러 발생:", err);
  res.status(500).json({
    error: "서버 내부 에러가 발생했습니다.",
    message: err.message,
  });
});

// 서버 시작
app.listen(port, async () => {
  console.log(`서버가 http://localhost:${port} 에서 실행 중입니다.`);
  await initializeData();
});
