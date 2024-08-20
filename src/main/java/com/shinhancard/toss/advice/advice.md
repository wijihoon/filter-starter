# GlobalExceptionHandler

`GlobalExceptionHandler`는 애플리케이션 전역에서 발생하는 예외를 처리하여 적절한 HTTP 응답을 생성하는 클래스입니다. 이 클래스는 `@ControllerAdvice`를 사용하여 예외를 잡아내고,
적절한 에러 응답을 클라이언트에게 반환합니다.

## 클래스 설명

- **패키지**: `com.shinhancard.toss.advice`
- **주요 어노테이션**:
    - `@ControllerAdvice`: 모든 컨트롤러에서 발생하는 예외를 처리합니다.
    - `@Slf4j`: 로그 기록을 위한 Lombok 어노테이션입니다.

## 예외 처리 메소드

### 1. `handleAuthenticationException`

- **설명**: `AuthenticationException` 발생 시 호출됩니다.
- **상세**: AuthenticationException의 `ErrorCode`에 따라 적절한 응답을 생성합니다.
- **매개변수**: `AuthenticationException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 2. `handleInvalidRedirectUrlException`

- **설명**: `InvalidRedirectUrlException` 발생 시 호출됩니다.
- **상세**: 유효하지 않은 리디렉션 URL에 대한 에러를 처리합니다.
- **매개변수**: `InvalidRedirectUrlException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 3. `handleInvalidTokenException`

- **설명**: `InvalidTokenException` 발생 시 호출됩니다.
- **상세**: 유효하지 않은 토큰에 대한 에러를 처리합니다.
- **매개변수**: `InvalidTokenException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 4. `handleJwtTokenException`

- **설명**: `JwtTokenException` 발생 시 호출됩니다.
- **상세**: JWT 토큰 관련 에러를 처리합니다.
- **매개변수**: `JwtTokenException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 5. `handleKafkaException`

- **설명**: `KafkaException` 발생 시 호출됩니다.
- **상세**: Kafka 관련 에러를 처리합니다.
- **매개변수**: `KafkaException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 6. `handleRedisConnectionException`

- **설명**: `RedisConnectionException` 발생 시 호출됩니다.
- **상세**: Redis 연결 관련 에러를 처리합니다.
- **매개변수**: `RedisConnectionException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 7. `handleRedisDataNotFoundException`

- **설명**: `RedisDataNotFoundException` 발생 시 호출됩니다.
- **상세**: Redis에서 데이터를 찾을 수 없을 때의 에러를 처리합니다.
- **매개변수**: `RedisDataNotFoundException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 8. `handleRedisOperationException`

- **설명**: `RedisOperationException` 발생 시 호출됩니다.
- **상세**: Redis 연산 관련 에러를 처리합니다.
- **매개변수**: `RedisOperationException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 9. `handleTokenCreationException`

- **설명**: `TokenCreationException` 발생 시 호출됩니다.
- **상세**: 토큰 생성 관련 에러를 처리합니다.
- **매개변수**: `TokenCreationException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 10. `handleTokenExpiredException`

- **설명**: `TokenExpiredException` 발생 시 호출됩니다.
- **상세**: 토큰 만료 에러를 처리합니다.
- **매개변수**: `TokenExpiredException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 11. `handleTokenRefreshException`

- **설명**: `TokenRefreshException` 발생 시 호출됩니다.
- **상세**: 토큰 갱신 관련 에러를 처리합니다.
- **매개변수**: `TokenRefreshException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 12. `handleUserNotFoundException`

- **설명**: `UserNotFoundException` 발생 시 호출됩니다.
- **상세**: 사용자 정보가 없을 때의 에러를 처리합니다.
- **매개변수**: `UserNotFoundException e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

### 13. `handleGeneralException`

- **설명**: 기타 모든 예외를 처리합니다.
- **상세**: 예상치 못한 에러를 처리합니다.
- **매개변수**: `Exception e` - 발생한 예외 객체
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`

## 공통 메소드

### `buildErrorResponse`

- **설명**: 에러 응답을 생성하는 공통 메소드입니다.
- **매개변수**:
    - `String message` - 에러 메시지
    - `HttpStatus status` - HTTP 상태 코드
- **반환 타입**: `ResponseEntity<ResponseVo<Object>>`
- **설명**: `ResponseVo` 객체를 사용하여 에러 응답을 생성하고 반환합니다.

---

이 클래스는 다양한 예외를 처리하여 클라이언트에게 적절한 에러 응답을 반환합니다. 각 예외에 대해 구체적인 처리 방법과 로그 기록을 통해 문제 발생 시 유용한 디버깅 정보를 제공합니다.
