## 프로젝트 소개

- 금리박사는 사용자가 다양한 은행의 예금 및 적금 상품을 검색, 비교할 수 있는 금융 상품 플랫폼입니다
- 이 시스템은 사용자 서비스와 관리자 서비스를 통해 금융 상품의 등록, 관리 및 사용자 맞춤형 서비스를 제공합니다!

## Information Architecture (IA)

![스크린샷 2025-01-06 154750](https://github.com/user-attachments/assets/487166ce-171d-4b18-b9df-3cbec993d167)

## ERD

![image](https://github.com/user-attachments/assets/33e56fbe-d5e5-4a79-98ce-d00dc125d539)

## 역할 분담
- **김진환 & 박상욱**
  - 사용자 회원가입 및 로그인, 회원 정보 수정, 회원 탈퇴, 로그아웃 기능 구현
  - JWT Token을 통한 stateless한 접속 상태 유지

- **오혜진**
  - 예금 & 적금 상품 목록 조회
  - 은행, 기간, 이자 계산방식, 가입 방식에 따른 필터링

- **김세현**
  - 비교 은행 추가, 삭제
  - 상품 상세 정보 조회
  - 이자 계산기
  - API 최신화 (외부 API 데이터 정제)

- **박채연**
  - 즐겨찾기 조회, 저장, 취소
 
- **조윤성**
  - 적금 달력 CRUD
 
- **양수민**
  - 이메일 문의
  - Q & A

- **오영수**
  - 방문자 현황 집계
  - 회원 조회
  - 관리자 1:1 문의
  - CI-CD

## 기술 스택

- Language : Java
- Framework : Spring Boot
- DataBase : MySQL, MongoDB, Redis
- Message Broker : Kafka

## System Diagram

![스크린샷 2025-01-06 155239](https://github.com/user-attachments/assets/2ff45897-d744-4f01-aa2a-932c4a596510)
