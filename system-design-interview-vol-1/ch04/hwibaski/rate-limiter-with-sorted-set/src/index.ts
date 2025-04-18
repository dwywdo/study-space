import { createClient } from "redis";
import { RateLimiter } from "./rate-limiter";

async function main() {
  const redisClient = await createClient({
    url: "redis://redis:6379",
  })
    .on("error", (err) => console.log("Redis Client Error", err))
    .connect();

  const rateLimiter = new RateLimiter({
    interval: 3000,
    maxInInterval: 2,
    minDifference: 1000,
    client: redisClient as any,
  });

  const result = await rateLimiter.limitWithInfo("user1");
  console.log(result); // blocked: false
  const result2 = await rateLimiter.limitWithInfo("user1");
  console.log(result2); // blocked: true - minDifference 초과
  const result3 = await rateLimiter.limitWithInfo("user1");
  console.log(result3); // blocked: true - maxInInterval 초과, minDifference 초과

  await sleep(3000);

  const result4 = await rateLimiter.limitWithInfo("user1");
  console.log(result4); // blocked: false

  const result5 = await rateLimiter.limitWithInfo("user1");
  console.log(result5); // blocked: true, - minDifference 초과
}

main();

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
