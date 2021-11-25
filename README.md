# 로컬 실행 방법

포팅메뉴얼 front 정보

- Vue2
- npm(v6.14.15)
- 웹서버 : nginx
- [k5d106.p.ssafy.io](http://k5d106.p.ssafy.io/) (SSL 적용)

프론트엔드 환경변수

- frontend 폴더 안 '.env.local' 파일 생성
- 'VUE_APP_SERVER_URL=https://k5d106.p.ssafy.io/api' 작성
- npm i 후 npm run serve로 실행

---------------------------------------------------------------------------------------------------

# 업무 분담

    1. 권준엽 : FE
    2. 박종익 : FE
    3. 권현지 : 동호회 팀, 공지사항, 일정, 이메일 인증, 안드로이드 APP
    4. 박범진 : 회원 인증, 경기 매칭, 게스트 찾기, 이미지 파일 처리, 알림
    5. 하지훈 : 배포, 채팅

---------------------------------------------------------------------------------------------------

# 기술 스택

    Java                    11
    Spring boot             2.5.5
    Vue.js                  2
    MySQL                   8.0.23
    DevOps                  Docker
    Android                 Pi

---------------------------------------------------------------------------------------------------
# DB

![DB.png](/picture/DB.png)

---------------------------------------------------------------------------------------------------

# 핵심 기능

### 1. 스포츠 동호회 관리
    1. 동호회 찾기(지역, 종목별)
    2. 공지사항
    3. 일정 관리
    4. 회원 관리
### 2. 경기 매칭/게스트 구하기
    1. 지역, 종목 별 경기리스트
    2. 경기 매칭 글 작성
### 3. 채팅 : 회원간의 채팅
### 4. 알림 : 팀 가입, 회원 신청 등과 같은 이슈에 알림


# 배포 화면
![로그인_화면](/picture/로그인_화면.png)
![메인](/picture/메인.png)
![스렌즈_리스트](/picture/스렌즈_리스트.png)
![게스트_찾기](/picture/게스트_찾기.png)
![채팅](/picture/채팅.png)
