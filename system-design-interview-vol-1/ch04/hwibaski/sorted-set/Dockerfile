# Node.js 앱을 위한 Dockerfile
FROM node:20-alpine

WORKDIR /usr/src/app

# 패키지 설치를 위한 레이어 분리
COPY package*.json ./
RUN npm install

# 애플리케이션 코드 복사
COPY app/index.js .

EXPOSE 3000

CMD ["node", "index.js"]
