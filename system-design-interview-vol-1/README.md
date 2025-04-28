# 가상 면접 사례로 배우는 대규모 시스템 설계 기초 1
## References
- [Book](https://product.kyobobook.co.kr/detail/S000001033116)
- [참고 링크](https://github.com/alex-xu-system/bytebytego/blob/main/system_design_links.md)

## Progress
### 2025-04-06
- Chatper 01 ~ Chapter 04
  1. 사용자 수에 따른 규모 확장성
  2. 개략적인 규모 측정
  3. 시스템 설계 면접 공략법
  4. 처리율 제한 장치의 설계
- 위 내용을 읽으면서 아래의 내용들을 생각해보기
  - 간단한 소감(?)
  - Chapter 04 를 읽으면서 더 학습해보고 싶다하는 내용
  - 이 책으로부터 어떤 것을 얻어가면 좋겠는지?
  - 이 책을 어떻게 공부하면 될지...?

공유받은 스터디 방식
```
1. Chapter 별로 담당자가 내용을 읽고, 가상의 상황을 설정하여 문제를 만들어온다.
2. 담당자는 기본 문제에 더해 심화 문제까지 만들어온다.
3. 스터디 진행 시 나머지 구성원은 면접보듯이 2인 1조로 문제의 요구사항을 만족하기 위한 설계를 진행한다.
4. 각자 발표 후 담당자는 정답을 발표한다.
5. 정답 발표 후 담당자는 심화 문제를 제출하고 다 같이 이야기해본다.
```

### 2025-04-10 (Cont. Chapter 04)
- 각자 읽은 내용에 대한 소감 공유
- 공유받은 스터디 방식을 바로 진행하기에는 무리가 있을 거라고 판단
- 책의 내용을 빨리 읽기보다는 내용을 최대로 소화한다는 생각을 기반으로
  1. 각자 추가적으로 학습해보고 싶은 목록을 공유
  2. 다음 스터디 시간까지 전부 다 진행할 필요 없음. 주어진 시간에 소화 가능한 만큼만
  3. 진행한 내용만큼만 공유. 
  4. "이제 다음 챕터로 넘어가도 되겠다" 싶을 때까지 반복
 
#### TODOs
- 공통
  - [Active-Active for Multi-Regional Resiliency](https://netflixtechblog.com/active-active-for-multi-regional-resiliency-c47719f6685b) 읽고 정리
  - [What the heck are you actually using NoSQL for?](https://highscalability.com/what-the-heck-are-you-actually-using-nosql-for/) 읽고 정리
- dwywdo
  - Lua Script와 Redis 실제 용례 (회사 코드 베이스로 동작 원리 파악 및 개선 포인트 생각해보기)
  - 내가 원하는 처리율 알고리즘을 Redis에서 선택할 수 있는지?
  - 각 처리율 알고리즘에 대한 적절한 사례 생각해보기
  - 실제 PoC 코드 같은 것도 작성해보면 좋을 것 같다 (순전히 선택사항)
- hwibaski
  - Lua Script 살펴보기
  - Redis Sorted Set 정리
  - AWS API Gateway에서 Rate Limiting 기능을 제공하는지 + 제공한다면 설정 방법
  - AWS 혹은 AWS가 아니더라도 Rate Limiting 기능을 위한 별도의 서비스가 있는지 조사

### 2025-04-17 (Cont. Chapter 04)
SKIP

### 2025-04-24 (Fin. Chapter 04)
끗.

### 2025-05-01 (Chapter 05)
Chapter 05 읽어오기 :)

#### TODOs
생기면 마음껏 추가하기 :)

- 공통
  - (placeholder)
- dwywdo
  - 읽기
    - https://blog.discord.com/scaling-elixir-f9b8ele7c296
    - https://theory.stanford.edu/~tim/s16/l/l1.pdf
  - Java에서 Consistent Hashing을 구현하는 데에 TreeMap 사용
    - TreeMap의 기반: Red-Black Tree
  - BST / AVL Tree / Red-Black Tree 간단히 복습
  - B Tree 계열 볼만한 자료
    - https://www.youtube.com/watch?v=liPSnc6Wzfk
- hwibaski
  - (placeholder)

#### 5. 안정 해시 설계

#### 6. 키-값 저장소 설계

#### 7. 분산 시스템을 위한 유일 ID 생성기 설계

#### 8. URL 단축기 설계

#### 9. 웹 크롤러 설계

#### 10. 알림 시스템 설계

#### 11. 뉴스 피드 시스템 설계

#### 12. 채팅 시스템 설계

#### 13. 검색어 자동완성 시스템

#### 14. 유튜브 설계

#### 15. 구글 드라이브 설계

#### 16. 배움은 계속된다

