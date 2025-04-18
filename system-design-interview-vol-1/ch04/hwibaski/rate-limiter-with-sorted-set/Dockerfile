# Node.js 앱을 위한 Dockerfile
FROM node:20-alpine

WORKDIR /usr/src/app

# 패키지 설치를 위한 레이어 분리
COPY package*.json ./
RUN npm install

# TypeScript 소스 코드 복사
COPY src/ ./src/
COPY tsconfig.json .

# TypeScript 컴파일 설정 및 빌드
RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]
