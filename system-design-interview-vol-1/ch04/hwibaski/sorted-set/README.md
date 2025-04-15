# Redis Sorted Set 실습

## 실습 코드

서버 시작

```bash
docker-compose up --build
```

서버 종료

```bash
docker-compose down
```

부하 테스트

```bash
# artillery 설치
npm install -g artillery

# 부하 테스트 실행
artillery run load-test.yml
```

## API 명세

### 1. 새로운 user 추가

```bash
curl -X POST http://localhost:3000/update-score \\n  -H "Content-Type: application/json" \\n  -d '{"member":"user1","score":500}'
```

### 2. 기존 user 점수 증가

```bash
curl -X POST http://localhost:3000/increment-score \\n  -H "Content-Type: application/json" \\n  -d '{"member":"user1","increment":100}'
```

### 3. 전체 score 조회

```bash
curl http://localhost:3000/
```
