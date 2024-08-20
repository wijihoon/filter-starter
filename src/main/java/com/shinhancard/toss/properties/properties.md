# CorsProperties 클래스

`CorsProperties` 클래스는 CORS 설정을 프로파일별로 정의합니다.

## 필드 설명

- **`allowedOrigins`**: 허용할 도메인 리스트입니다.
    - **타입**: `List<String>`
    - **기본값**: `Collections.emptyList()`
    - **제약 조건**: 최소 하나 이상의 도메인이 설정되어야 함.

- **`allowedMethods`**: 허용할 HTTP 메서드 리스트입니다.
    - **타입**: `List<String>`
    - **기본값**: `List.of("GET", "POST", "PUT", "DELETE")`
    - **제약 조건**: 최소 하나 이상의 메서드가 설정되어야 함.

- **`allowedHeaders`**: 허용할 HTTP 헤더 리스트입니다.
    - **타입**: `List<String>`
    - **기본값**: `List.of("Authorization", "Content-Type")`
    - **제약 조건**: 최소 하나 이상의 헤더가 설정되어야 함.

- **`allowCredentials`**: 자격 증명 포함 요청 허용 여부입니다.
    - **타입**: `boolean`
    - **기본값**: `true`

## 메서드 설명

- **`validateProperties()`**: 설정된 값들의 유효성을 검증합니다.
    - **예외**: `IllegalArgumentException` (허용된 메서드 또는 헤더 리스트가 비어 있는 경우)

## 클래스 설명

- **`@ConfigurationProperties(prefix = "cors")`**: `application.yml`의 `cors` 접두사에 매핑됩니다.
- **`@Validated`**: 유효성 검사를 활성화합니다.
- **`@Getter` / `@Setter`**: Lombok의 어노테이션으로 getter 및 setter 메서드를 자동 생성합니다.
- **`@PostConstruct`**: 빈 초기화 후 유효성 검증을 수행합니다.

---

# JwtTokenProperties 클래스

`JwtTokenProperties` 클래스는 JWT 관련 설정을 관리합니다.

## 필드 설명

- **`secret`**: JWT 비밀 키입니다.
    - **타입**: `String`
    - **기본값**: `"defaultSecretKey"`

- **`validity`**: JWT 토큰 유효 기간 (밀리초 단위)
    - **타입**: `long`
    - **기본값**: `3600000L` (1시간)
    - **제약 조건**: 0보다 커야 함.

- **`refreshValidity`**: JWT 리프레시 토큰 유효 기간 (밀리초 단위)
    - **타입**: `long`
    - **기본값**: `86400000L` (24시간)
    - **제약 조건**: 0보다 커야 함.

## 메서드 설명

- **`validateProperties()`**: 설정된 값들의 유효성을 검증합니다.
    - **예외**: `IllegalArgumentException` (유효 기간이 0 이하인 경우)

## 클래스 설명

- **`@ConfigurationProperties(prefix = "jwt")`**: `application.yml`의 `jwt` 접두사에 매핑됩니다.
- **`@Validated`**: 유효성 검사를 활성화합니다.
- **`@Getter` / `@Setter`**: Lombok의 어노테이션으로 getter 및 setter 메서드를 자동 생성합니다.
- **`@PostConstruct`**: 빈 초기화 후 유효성 검증을 수행합니다.

---

# LoggingProperties 클래스

`LoggingProperties` 클래스는 애플리케이션의 로그 설정을 구성합니다.

## 필드 설명

- **`requestBody`**: 요청 본문 관련 설정을 담는 객체입니다.
    - **타입**: `RequestBody`
    - **기본값**: 새로운 `RequestBody` 객체

- **`responseBody`**: 응답 본문 관련 설정을 담는 객체입니다.
    - **타입**: `ResponseBody`
    - **기본값**: 새로운 `ResponseBody` 객체

- **`body`**: 로그 본문 크기 관련 설정을 담는 객체입니다.
    - **타입**: `Body`
    - **제약 조건**: `@NotNull`
    - **기본값**: 새로운 `Body` 객체

## 내부 클래스

- **`RequestBody`**: 요청 본문 설정.
    - **필드**:
        - **`truncate`**: 요청 본문 자르기 여부.
            - **타입**: `boolean`
            - **기본값**: `false`

- **`ResponseBody`**: 응답 본문 설정.
    - **필드**:
        - **`truncate`**: 응답 본문 자르기 여부.
            - **타입**: `boolean`
            - **기본값**: `false`

- **`Body`**: 로그 본문 크기 설정.
    - **필드**:
        - **`maxSize`**: 최대 본문 크기 (바이트 단위).
            - **타입**: `int`
            - **기본값**: `10000`
            - **제약 조건**: `@Min(value = 1, message = "Max size for log body must be greater than zero.")`

## 메서드 설명

- **`validateProperties()`**: 설정을 검증하는 메서드입니다.
    - **예외**: `IllegalArgumentException` (로그 본문 크기 값이 0 이하인 경우)

## 클래스 설명

- **`@ConfigurationProperties(prefix = "logging")`**: `application.yml`의 `logging` 접두사에 매핑됩니다.
- **`@Validated`**: 유효성 검사를 활성화합니다.
- **`@Getter` / `@Setter`**: Lombok의 어노테이션으로 getter 및 setter 메서드를 자동 생성합니다.
- **`@PostConstruct`**: 빈 초기화 후 유효성 검증을 수행합니다.

---

# RedisProperties 클래스

`RedisProperties` 클래스는 Redis 서버와의 연결 설정을 관리합니다.

## 필드 설명

- **`host`**: Redis 서버의 호스트명.
    - **타입**: `String`
    - **기본값**: `"localhost"`
    - **제약 조건**: `@NotBlank`

- **`port`**: Redis 서버의 포트 번호.
    - **타입**: `int`
    - **기본값**: `6379`
    - **제약 조건**: `@Min(value = 1024, message = "Redis port must be at least 1024")`

- **`password`**: Redis 서버의 비밀번호.
    - **타입**: `String`
    - **기본값**: `null`

- **`timeout`**: Redis 커넥션 타임아웃 (밀리초 단위).
    - **타입**: `int`
    - **기본값**: `2000`
    - **제약 조건**: `@Min(value = 1000, message = "Redis timeout must be at least 1000 milliseconds")`

## 메서드 설명

- **`validateProperties()`**: 설정을 검증하는 메서드입니다.
    - **검증 내용**:
        - **호스트**: `host`가 null 또는 빈 값인 경우 `IllegalArgumentException` 발생.
        - **포트**: `port`가 1024 이상 65535 이하인지 검증.
        - **타임아웃**: `timeout`이 1000ms 이상인지 검증.

## 클래스 설명

- **`@ConfigurationProperties(prefix = "spring.redis")`**: `application.yml`의 `spring.redis` 접두사에 매핑됩니다.
- **`@Validated`**: 유효성 검사를 활성화합니다.
- **`@Getter` / `@Setter`**: Lombok의 어노테이션으로 getter 및 setter 메서드를 자동 생성합니다.
- **`@PostConstruct`**: 빈 초기화 후 유효성 검증을 수행합니다.
