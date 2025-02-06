## KUBUS 백엔드

### 파일구조

#### 루트 디렉토리 설정 파일

- `build.gradle` - Gradle 빌드 설정 파일
- `docker-compose.yml` - Docker 컨테이너 구성 파일
- `Dockerfile` - Docker 이미지 빌드 설정
- `application.yml` - Spring Boot 애플리케이션 설정 파일 (src/main/resources 내에 위치)

#### 소스 코드 구조 (src/main/java/com/phishme/backend)

- **commons/** - 공통 유틸리티 클래스

  - `OIDCJwtValidator` - OAuth/OIDC JWT 토큰 검증
  - `SnowFlakeGenerator` - 고유 ID 생성기
  - `StringEncryptor` - 문자열 암호화 유틸리티
  - `ValidateUtils` - 데이터 유효성 검증 유틸리티

- **controller/** - REST API 엔드포인트 컨트롤러

  - `AuthController` - 인증 관련 API
  - `TermController` - 약관 관련 API
  - `UserController` - 사용자 관련 API

- **dto/** - 데이터 전송 객체

  - `login/` - 로그인 관련 DTO
  - `register/` - 회원가입 관련 DTO
  - `user/` - 사용자 정보 관련 DTO
  - 기타 공통 DTO (BadWordFilter, Error, Term 등)

- **entities/** - JPA 엔티티 클래스

  - `JwtTokens` - JWT 토큰 저장
  - `Notices` - 공지사항
  - `Terms` - 서비스 약관
  - `TermAgreement` - 약관 동의 내역
  - `TrainProgress` - 학습 진행 상태
  - `Users` - 사용자 정보

- **enums/** - 열거형 타입

  - `AuthProviderType` - 인증 제공자 유형
  - `ErrorCode` - 오류 코드
  - `GenderType` - 성별 유형
  - `TokenStatus` - 토큰 상태
  - `UserRoles` - 사용자 권한

- **exceptions/** - 예외 처리

  - `BusinessException` - 비즈니스 로직 예외
  - `ExceptionHandlerFilter` - 필터 단계 예외 처리
  - `GlobalExceptionHandler` - 전역 예외 처리

- **repositories/** - JPA 리포지토리

  - `JwtTokenRepository` - JWT 토큰 관리
  - `TermRepository` - 약관 관리
  - `TermAgreementRepository` - 약관 동의 관리
  - `UserRepository` - 사용자 정보 관리

- **security/** - 보안 설정

  - `enc/` - 암호화 관련 설정
  - `jwt/` - JWT 인증 관련 클래스
    - `CustomUserDetailService` - 사용자 상세 정보 서비스
    - `JwtAuthenticationFilter` - JWT 인증 필터
    - `JwtGenerator` - JWT 토큰 생성
    - `JwtService` - JWT 관련 서비스
    - `JwtUtil` - JWT 유틸리티
  - `EncryptionConfig` - 암호화 설정
  - `SecurityConfig` - Spring Security 설정
  - `WebMvcConfig` - Spring MVC 설정

- **service/** - 비즈니스 로직 서비스
  - `impl/` - 서비스 구현체
    - `TermServiceImpl` - 약관 서비스 구현
    - `UserServiceImpl` - 사용자 서비스 구현
  - `TermService` - 약관 서비스 인터페이스
  - `UserService` - 사용자 서비스 인터페이스

### 개발환경 환경변수 설정

1. 프로젝트 루트폴더에 .env파일 생성
2. 아래의 포멧에 내용을 채워넣은후 저장

```
SPRING_DATASOURCE_URL=jdbc:mariadb://<호스트>:<포트번호>/<DB이름>
SPRING_DATASOURCE_USERNAME=<DB로그인 ID>
SPRING_DATASOURCE_PASSWORD=<DB로그인 PW>

AES_ENCRYPTION_KEY=<AES 암호화 키 (Base64로 인코딩된 상태)>

JWT_ACCESS_SECRET=<JWT Access Token 비밀키>
JWT_REFRESH_SECRET=<JWT Refresh Token 비밀키>
JWT_ACCESS_EXPIRATION=21600000
JWT_REFRESH_EXPIRATION=2629746000

KAKAO_CLIENT_ID=<카카오톡 클라이언트ID>
APPLE_CLIENT_ID=<프론트 패키지 이름>
GOOGLE_IOS_CLIENT_ID=<구글 IOS용 클라이언트 ID>
GOOGLE_ANDROID_CLIENT_ID=<구글 Android용 클라이언트 ID>

PROFANITY_API_KEY=<비속어 검증 서비스 profanity의 API 키>
```

### 배포환경 구성

- docker-compose.yml 내부의 환경변수 작성
- docker-compose up 하면댐요
