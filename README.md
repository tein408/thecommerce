# User Management System
이 프로젝트는 사용자 관리 시스템의 백엔드를 구현한 것입니다. 사용자의 회원가입, 정보 수정, 목록 조회 기능을 제공합니다.

# 프로젝트 설치 및 실행 방법 (macOS 기준)
1. 의존성 설치: 터미널에서 프로젝트를 클론한 후, Gradle을 사용하여 의존성을 설치합니다.
```bash
./gradlew build
```

2. 데이터베이스 설정: 이 애플리케이션은 H2 데이터베이스를 사용하며, 별도의 데이터베이스 설정이 필요하지 않습니다.

3. 애플리케이션 실행: 터미널에서 애플리케이션을 실행합니다.
```bash
./gradlew bootRun
```

4. API 사용: API를 사용하여 회원가입, 정보 수정, 목록 조회 등의 기능을 이용할 수 있습니다.

# API 문서
API 문서는 Swagger를 통해 제공됩니다.  
애플리케이션을 실행한 후 http://localhost:8080/swagger-ui/index.html 로 접속하여 확인할 수 있습니다.

# 테이블 구조
이 애플리케이션은 다음과 같은 테이블을 사용합니다:

|컬럼명|type|설명|
|------|---|---|
|userIndex|Long|PK|
|userId|String|회원 아이디|
|userName|String|회원 이름|
|email|String|이메일|
|password|String|비밀번호|
|phoneNumber|String|전화번호|
|createDate|LocalDateTime|회원 가입 날짜|

# 개발 환경
Java 8   
Spring Boot 2.6.2   
Spring Data JPA 2.6.0   
Spring Security 5.6.1   
H2 Database 1.4.200   
Lombok 1.18.22   
Swagger 3.0.0   
Slf4j 1.7.32

# Test Coverage 확인
터미널에서 아래의 명령어를 순서대로 입력한 후 프로젝트 폴더 내의 
`thecommerce/user/build/reports/jacoco/test/html/` 경로에서 `index.html` 을 실행하여 확인할 수 있습니다.
```bash
./gradlew test
./gradlew check
```

# 프로젝트 구조
> 프로젝트 구조는 아래와 같습니다.
```text
user
┣ bin
┣ build
┣ gradle
┣ src
┃ ┣ main.java.com.thecommerce
┃ ┃ ┗ user
┃ ┃ ┃ ┣ config
┃ ┃ ┃ ┃ ┣ SwaggerConfig.java
┃ ┃ ┃ ┃ ┣ WebConfig.java
┃ ┃ ┃ ┃ ┗ WebSecurityConfig.java
┃ ┃ ┃ ┣ user
┃ ┃ ┃ ┃ ┣ status
┃ ┃ ┃ ┃ ┃ ┣ UserRegistrationStatus.java
┃ ┃ ┃ ┃ ┃ ┗ UserUpdateStatus.java
┃ ┃ ┃ ┃ ┣ userDTO
┃ ┃ ┃ ┃ ┃ ┣ UpdateUserDTO.java
┃ ┃ ┃ ┃ ┃ ┣ UserDTO.java
┃ ┃ ┃ ┃ ┃ ┗ UserListDTO.java
┃ ┃ ┃ ┃ ┣ User.java
┃ ┃ ┃ ┃ ┣ UserController.java
┃ ┃ ┃ ┃ ┣ UserRepository.java
┃ ┃ ┃ ┃ ┗ UserService.java
┃ ┃ ┃ ┗ UserApplication.java
┃ ┃ ┗ resources
┃ ┃ ┃ ┣ static
┃ ┃ ┃ ┣ templates
┃ ┃ ┃ ┗ application.yml
┃ ┗ test
┃ ┃ ┗ java.com.thecommerce
┃ ┃ ┃ ┗ user
┃ ┃ ┃ ┃ ┣ UserApplicationTests.java
┃ ┃ ┃ ┃ ┣ UserControllerTests.java
┃ ┃ ┃ ┃ ┗ UserServiceTests.java
┣ build.gradle
┣ gradle.properties
┣ gradlew
┣ gradlew.bat
┗ settings.gradle
```