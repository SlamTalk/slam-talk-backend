<h1 align="middle">Slam Talk (슬램톡)</h1>

<p align="middle">
<img src="https://github.com/SlamTalk/slam-talk-frontend/assets/103404125/a6bd1eca-7d78-402c-99a1-66005ecc1727" width="200"/>
</p>

<p align="middle">농구를 할 장소와 함께 할 친구를 찾을 수 있는 플랫폼</p>

<p align="middle"><a href="https://www.slam-talk.site">사이트 바로가기 ⛹️‍♀️⛹️‍♂️</a></p>

## 프로젝트 개요

> 개발 기간: 24/01/11 ~ 24/2/22(프로젝트 발표, 구름 수료) 이후 유지보수 중<br>
>
> [동기와 비동기 팀 노션](https://www.notion.so/7460cade2e63406481e110249fc6f991) | [프론트 노션](https://www.notion.so/7460cade2e63406481e110249fc6f991?p=a0f8672e41df49ce86c681506b707aeb&pm=s) | [백엔드 노션](https://platinum-roof-8a7.notion.site/687767f0b9904f8f8fe8a73ffc83d159?pvs=4) <br> [Swagger](http://43.200.131.233:8080/swagger-ui/index.html) | [API 문서](https://www.notion.so/7460cade2e63406481e110249fc6f991?p=f3bf16cf100e45f69a3e0bb075a342b0&pm=s) | [ERD](https://www.erdcloud.com/d/GyK7pkbTanPFqno4F)
>
> [기획서 & 기능 명세서](https://www.notion.so/ec211098ba794bff83e6a41a74a3d58c)

## 동기와 비동기 백엔드 팀원 소개

> [백엔드 팀원 소개 바로가기]()

<table width="500" align="center">
<tbody>
<tr>
<th>Pictures</th>
<td width="100" align="center">
<a href="https://github.com/Jiiker">
<img src="https://avatars.githubusercontent.com/u/83278069?v=4" width="60" height="60">
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/hi-rachel">
<img src="https://avatars.githubusercontent.com/u/101985441?v=4" width="60" height="60">
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/SwimmingRiver">
<img src="https://avatars.githubusercontent.com/u/75059684?v=4" width="60" height="60">
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/SwimmingRiver">
<img src="https://avatars.githubusercontent.com/u/72694104?v=4" width="60" height="60">
</a>
</td>
</tr>
<tr>
<th>Name</th>
<td width="100" align="center">홍예지</td>
<td width="100" align="center">이봉승</td>
<td width="100" align="center">전성환</td>
<td width="100" align="center">류동수</td>

</tr>
<tr>
<th>Role</th>
<td width="300" align="left">
<div align='center'></div>
<ul>
백엔드 파트장
<li>채팅 서비스 구현
<ul>
<li>실시간 채팅</li>
<li>최초 입장 및 나가기 이벤트 설정</li>
<li>채팅방 유형별 생성 및 관리(1:1,농구장,모집글(같이하기/팀매칭))</li>
<li>채팅방 과거 내역 조회</li>
<li>채팅 리스트 조회</li>
</ul>
</li>

<li>s3bucket 연결 및 이미지 업로드 설정</li>

</ul>

</td>
<td width="300" align="left">
<ul>
<li>AWS CI/CD 환경 구축</li>
<li>kakao, google, naver 소셜 로그인 구현</li>
<li>자체 이메일 로그인</li>
<li>SMTP 이메일 인증</li>
<li>마이페이지</li>
</ul>
</td>

<td width="300" align="left">
<ul>
<li>농구장 지도 조회 기능</li>
<li>이용자 농구장 제보 & 조회 기능</li>
<li>관리자 제보 농구장 조회 & 수락 기능</li>
<li>커뮤니티 게시글 기능</li>
<li>커뮤니티 댓글 기능</li>
<li>커뮤니티 태그 분류 기능</li>
</ul>
</td>
<td width="300" align="left">
<ul>
<li>농구메이트/상대팀 매칭 서비스 구현
<ul>
<li>모집 글 등록, 조회, 삭제, 편집 (CRUD)</li>
<li>매칭 모집 글 지원하기</li>
<li>게시물 필터링(약속시간 경과 여부/포지션/실력별/지역별)</li>
</ul>
</li>
</ul>
</td>
</tr>
<tr>
<th>GitHub</th>
<td width="100" align="center">

<a href="https://github.com/yj120">
<img src="http://img.shields.io/badge/yj120-green?style=social&logo=github"/>
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/leebongseung">
<img src="http://img.shields.io/badge/leebongseung-green?style=social&logo=github"/>
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/sh-9610">
<img src="http://img.shields.io/badge/sh-9610green?style=social&logo=github"/>
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/red481">
<img src="http://img.shields.io/badge/red481-green?style=social&logo=github"/>
</a>
</td>
</tr>
</tbody>
</table>

<br>

## 프로젝트 소개

> 프론트엔드 3명, 백엔드 4명으로 구성되어 작업한 프로젝트입니다. <br>
> 소개글 작성 <br>

### 프로젝트 아키텍처 <br>

<img alt="슬램톡 프로젝트 아키텍처" src="https://github.com/SlamTalk/slam-talk-backend/assets/101985441/f81998a1-ac8c-4049-8591-e7ac4de80dcd" width="1200" /> <br>

### ERD <br>

<img alt="슬램톡 프로젝트 아키텍처" src="https://github.com/SlamTalk/slam-talk-backend/assets/101985441/6d8154ae-353e-4249-a8ed-214027df8a8e" width="1200" /> <br>

### 기술 스택

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=JSONWebTokens&logoColor=white">
<br>

<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">  <img src="https://img.shields.io/badge/hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white">  <img src="https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white">  <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">  <img src="https://img.shields.io/badge/swaager-85EA2D?style=for-the-badge&logo=swagger&logoColor=white">
<br>

<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">  <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">  <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">  <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">  <img src="https://img.shields.io/badge/letsencrypt-003A70?style=for-the-badge&logo=letsencrypt&logoColor=white">
<br>

<img src="https://img.shields.io/badge/-Git-F05032?style=for-the-badge&logo=git&logoColor=ffffff">  <img src="https://img.shields.io/badge/Github Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">  <img src="https://img.shields.io/badge/Intellij IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">

[기술 스택 선정 이유](https://platinum-roof-8a7.notion.site/d0513167b2e5483196f4308e4e4484e3?pvs=4)

## 주요 기능

### 메인 페이지

### 회원가입

<img alt="회원가입" src="https://github.com/SlamTalk/slam-talk-frontend/assets/103404125/9192f835-7ffd-4f25-aeab-e43faec4f997" width="400px">

- 자체 회원가입
    
    이메일 인증을 통해 인증코드를 받는 방식으로 회원가입이 진행됩니다. 받은 인증코드를 정확하게 입력한 후, 닉네임과 비밀번호를 설정하면 회원가입이 완료됩니다.
    
- 소셜 회원가입
    
    Google, Kakao, Naver 중 하나를 통해 로그인을 시도하실 경우, 최초 로그인 시 해당 계정의 닉네임, 이메일, 프로필 이미지 정보를 사용하여 자동 회원가입이 진행됩니다.
    
- 마이 프로필 보기
    
    사용자는 자신의 프로필 이미지, 닉네임, 자기소개, 포지션 등의 정보를 자유롭게 수정할 수 있습니다.
    
- 유저 레벨 시스템
    
    사용자의 팀 매칭 활동, 게시글 작성, 출석, 제보 등의 활동 내역을 기반으로 레벨과 포인트를 산정합니다. 이를 통해 사용자의 활동 수준과 획득한 포인트를 확인할 수 있습니다.
    

### 로그인 유저 관리

<img alt="로그인-유저-관리" src="https://github.com/SlamTalk/slam-talk-frontend/assets/103404125/16eeecd8-1ac2-4537-bfce-8f8786abfd34" width="400px">

- 로그인
    
    로그인한 유저에게 헤더를 통해 accessToken 및 쿠키를 통한 RefreshToken이 발급됩니다. 각각 유효기간은 1시간과 7일로 설정하였습니다.
    
- 로그아웃
    
    로그아웃 시 DB에 기록된 유저의 RefreshToken에 관한 정보를 삭제 하고 유저의 쿠키 정보를 삭제합니다
    
- 상대방 프로필 표시
    
    상대방 유저의 아바타를 누르면 개인정보(이메일, 소셜타입)를 제외한 프로필 정보를 보여줍니다.
    
- 회원 탈퇴
    
    현재 유저의 게시물 및 채팅 기록은 살려두고 유저는 soft Delete 처리가 됩니다
    

### 농구장 지도

### 제보하기

### 시합 상대팀 찾기
https://github.com/SlamTalk/slam-talk-frontend/assets/100774811/bdf6fc4e-6483-4d99-a624-085a9b0737cc
- 상대팀 찾기 새 모집글 작성

  제목, 팀명, 장소, 날짜와 시간, 규모, 원하는 실력대를 필수 입력 필드로 하고, 필드 값을 넣어 '모집 완료' 버튼을 누르면 새 모집글이 생성됩니다.

  > 주소 선택시 카카오 지도 API를 활용하여 검색한 곳 또는 마우스 클릭으로 표시한 지점에 마커를 찍어주고, 확인 버튼을 눌렀을 때 해당 마커가 있는 곳의 주소가 주소 입력필드로 넘어가게 됩니다.


https://github.com/SlamTalk/slam-talk-frontend/assets/100774811/b2f649e5-8fd5-44aa-add4-a8e813097ba3
- 상대팀 찾기 모집글 지원하기
  
  유저가 해당 모집글의 작성자와 다른 사람일 경우 모집글 상세페이지 하단에 '지원하기' 버튼이 표시되고, 팀명과 실력 필드 값을 넣어 '지원하기' 버튼을 누르면 해당 모집글의 지원자로 등록됩니다.

  본인이 지원한 리스트는 '취소' 버튼을 통해 취소할 수 있습니다.

https://github.com/SlamTalk/slam-talk-frontend/assets/100774811/e941e069-cd2f-4a8c-a8f1-92c02f6d3f0a
- 상대팀 찾기 모집글 지원자 수락 및 모집완료
  
  유저가 해당 모집글의 작성자와 동일한 경우 모집글 상세페이지 하단에는 '모집 완료' 버튼과 '수정' 버튼이 표시되고, 대기중인 지원자 리스트에는 '수락'과 '거절' 버튼이 표시됩니다.

  '수락' 버튼을 눌러 지원자의 신청을 수락할 수 있고, '모집 완료' 버튼을 통해 모집중인 글을 마감할 수 있습니다.

  '모집 완료' 버튼을 누르면 해당 모집글은 모집 완료 상태로 변경되고, ACCEPTED된 지원자와 팀채팅(MM)이 생성됩니다.


### 농구 메이트 찾기

https://github.com/SlamTalk/slam-talk-frontend/assets/100774811/7314571a-0cc2-4e7f-99b8-a4803a373263
- 메이트 찾기 새 모집글 작성

  제목, 장소, 날짜와 시간, 포지션 별 인원 수, 원하는 실력대를 필수 입력 필드로 하고, 필드 값을 넣어 '모집 완료' 버튼을 누르면 새 모집글이 생성됩니다.

  > 포지션 별 인원 수의 경우 각각의 모집 인원을 다 더한 값이 0인 경우에 예외처리를 해주어서 어떤 포지션이든 1명 이상 모집해야 글을 작성할 수 있도록 했습니다.

  > 주소 선택시 카카오 지도 API를 활용하여 검색한 곳 또는 마우스 클릭으로 표시한 지점에 마커를 찍어주고, 확인 버튼을 눌렀을 때 해당 마커가 있는 곳의 주소가 주소 입력필드로 넘어가게 됩니다.


https://github.com/SlamTalk/slam-talk-frontend/assets/100774811/a8a4738a-7665-4c44-9793-aa5db87df575

- 메이트 찾기 모집글 지원하기
  
  유저가 해당 모집글의 작성자와 다른 사람일 경우 모집글 상세페이지 하단에 '지원하기' 버튼이 표시되고, 포지션과 실력 필드 값을 넣어 '지원하기' 버튼을 누르면 해당 모집글의 지원자로 등록됩니다.

  본인이 지원한 리스트는 '취소' 버튼을 통해 취소할 수 있습니다.


https://github.com/SlamTalk/slam-talk-frontend/assets/100774811/381169d3-c77c-40b3-be49-72223c0ef304

- 메이트 찾기 모집글 지원자 수락 및 모집완료
  
  유저가 해당 모집글의 작성자와 동일한 경우 모집글 상세페이지 하단에는 '모집 완료' 버튼과 '수정' 버튼이 표시되고, 대기중인 지원자 리스트에는 '수락'과 '거절' 버튼이 표시됩니다.

  '수락' 버튼을 눌러 지원자의 신청을 수락할 수 있고, '모집 완료' 버튼을 통해 모집중인 글을 마감할 수 있습니다.

  '모집 완료' 버튼을 누르면 해당 모집글은 모집 완료 상태로 변경되고, ACCEPTED된 지원자들과 메이트 채팅(TM)이 생성됩니다.

### 커뮤니티

### 채팅

인증된 유저 끼리 실시간으로 채팅을 이용할 수 있습니다.

채팅방 타입은 1:1 채팅방 / 농구장 채팅방 / 같이하기 채팅방 / 팀 매칭 채팅방 이렇게 4가지 종류가 있으며,

각 타입에 따라 채팅방 이름이 결정됩니다. 

채팅방 첫 입장시 입장안내 메시지,퇴장시에는 퇴장안내 메시지를 채팅방에 보여줍니다.

유저가 좌측 상단에있는 나가기 버튼을 누르면 다른 페이지로 이동을 합니다.(완전한 퇴장 x)

→ 유저가 채팅방에서 마지막으로 본 메시지를 기록하고, 재입장시에는 그 이후의 메시지만 보여줍니다.

또한 채팅방에서 과거 메세지를 조회하고 싶다면 more 버튼을 눌러서 과거 메세지 내역을 조회할 수 있습니다.

<img alt="1:1-채팅" src="https://github.com/SlamTalk/slam-talk-frontend/assets/103404125/2c8fa872-70b4-48c8-bc64-90853044f195" width="400px" height="560px">

1:1 채팅방

1:1 채팅방은 채팅방을 생성할 유저 프로필 카드에서 또는 팀 매칭 완료후 팀 대표자 간 채팅방 생성이 가능합니다.

<img alt="시설-채팅" src="https://github.com/SlamTalk/slam-talk-frontend/assets/103404125/85c88777-2e27-407d-86aa-63c8f3cb08bf" width="400px" height="560px">

단체채팅방

단체 채팅방은 농구장 지도, 메이트 매칭에서 생성 가능합니다.

채팅 리스트에서는 유저가 참여한 채팅방 목록을 조회할 수 있습니다.

채팅방 최신 메세지, 채팅방 제목, 채팅방 타입(DM/BM/MM/TM) 을 함께 확인할 수 있습니다.

<img alt="채팅-리스트" src="https://github.com/SlamTalk/slam-talk-frontend/assets/103404125/936b88a5-67a5-4646-9745-97425f3cdbfe" width="400px">

채팅 방 타입에 따라서 정해진 이름과 dm일 경우는 유저의 프로필이 채팅룸의 이미지가 됩니다.

타입별로 구분하여 명시해줍니다.

### 관리자 페이지

## Problem Solving

> 문제 발생과 해결 방법


### 소셜로그인 시 닉네임 중복 해결 - 이봉승

- 소셜로그인 시 닉네임 중복 해결하기
    
    기존 소셜에서 제공하는 닉네임을 사용하여 그대로 적용해주기로 요구사항을 정리했습니다. 구현하기에 앞서 아래와 같은 문제를 정의하였습니다.
    
    문제원인 
    
    1. 소셜 닉네임과 저희 서비스와의 동일한 닉네임이 존재하는 경우
    2. 소셜 닉네임의 길이가 13자를 초과하는 경우
    
    해결방법
    
    NicknameService라는 클래스를 만들어서 기존 소셜 닉네임의 길이 검증 ⇒ 중복 여부 판별을 하였습니다.
    
    - 중복이 되었을 경우 “익명” + Random숫자(최대 11자)를 생성해서 익명닉네임을 부여하는 방법으로 유저에게 최대한 기존 소셜 닉네임을 사용할 수 있도록 편의성을 제공해주었습니다.

### 엑세스 토큰 및 리프레시 토큰 저장 방식 - 이봉승

보안문제점 : 클라이언트에서 항상 필요로 하는 엑세스 토큰, 액세스 토큰을 재발급하기위한 리프레쉬 토큰을 어떻게 저장할지가 가장 어려웠습니다.

- 엑세스 토큰은 헤더에, 리프레쉬 토큰은 쿠키를 통해 클라이언트에 넘겨주기!
    
    결정 방법 : 엑세스 토큰의 경우 클라이언트에서 메모리에 저장하여 XSS 공격을 방지하기로 하였고, 리프레쉬 토큰은 HttpOnly Secure 쿠키로 설정하였습니다.
    
    결정 이유 : 
    
    - 엑세스 토큰의 경우 메모리에 저장하기 때문에 탈취 시 피해를 최소화 하기위하여 유효시간을 짧게 1시간으로 잡았습니다.
    - 리프레쉬 토큰의 경우 클라이언트에서 별도의 관리를 할 필요가 없으므로 쿠키에 저장하여 HttpOnly Secure 방법으로 XSS 공격 방지 및 SameSite를 Lax로 설정하여서 CSRF 공격을 방지하도록 설정하였습니다.
    - 헤더 vs 쿠키(장점) 왜 쿠키인지
        
        헤더로 주면 클라이언트 스크립트로 추출해서 저장되는데 ← XSS(**Cross-Site Scripting)** 공격 가능해서 쿠키를 선택하게 되었다. 쿠키 선택시에는 CSRF 공격이 가능해서 SameSite설정을 해야한다.
        

### 채팅 - 홍예지

- 채팅방 생성 시 중복 검사
    
    동일한 채팅방에 참여하고 있음에도 불구하고 동일한 채팅방을 생성하려고 할 때 기존에 존재하는 채팅방인지 검사하는 로직을 추가하였습니다.
    
    1:1 채팅방은 채팅방 정보에 상대방의 유저 아이디를 저장해두어 A유저가 B유저 서로 동일한 채팅방을 중복으로 생성하지 않도록 하였습니다.
    같이하기 채팅방과  팀매칭 채팅방은 각 모집글의 아이디를 저장하여 중복 생성을 방지 하였고, 모집이 완료된 시점에서 채팅방 생성을 단 한번만 할 수 있도록 하여 클라이언트 레벨에서도 중복 생성을 할 수 없게 설정하였습니다.
    
- 리스트 조회 시 채팅방 타입에 따른 정보 반환
    
    채팅방 타입에 따라 채팅 리스트에서 보여야 하는 내용이 상이하기 때문에 유형별로 채팅방 제목, 채팅방 이미지가 다르게 보일 수 있도록 설정했습니다.
    
- redis를 통한 과거 메세지 내역 조회
    
    유저가 참여한 채팅방 마다 readIndex 라는 값을 유저가 참여한 채팅방 정보에 함께 저장하였습니다. 
    그리고 이 readIndex 를 기준으로 과거 내역을 요청할 수 있도록 설정하였습니다.
    
    특히 메세지가 전송될 때 DB와 Redis 에 모두 저장이 되게 하여 과거 내역을 조회할 때도 Redis 를 먼저 조회하여 DB 에 직접 접근하는 횟수를 줄여 성능을 향상 시켰습니다.
    또한 Redis TTL 을 12시간으로 설정하여 저장 공간을 효율적으로 사용할 수 있게 하였습니다.

   
## 협업 방식

> 문서: Notion 활용 <br>
> 소통: Slack, KakaoTalk 활용

[깃 컨벤션](https://www.notion.so/300caffe87af4fb09eaea24d3cfc31c7) <br>

## 기타

- 구름 풀스택 2회차 최종 프로젝트 인기상 수상 - [발표(24/02/22) PPT](https://github.com/SlamTalk/slam-talk-frontend/files/14399752/slam_talk.pdf)
- [피그마 와이어프레임](https://www.figma.com/file/AAC8YMtbWw32jkT0XNRS9q/%EB%86%8D%EA%B5%AC-%EC%95%B1?type=design&node-id=0%3A1&mode=design&t=fOLsFQ3RiSAo5Rml-1)
