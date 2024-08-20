# JwtAuthenticationFilter 클래스

`JwtAuthenticationFilter` 클래스는 Spring Security의 `UsernamePasswordAuthenticationFilter`를 확장하여 JWT 토큰 기반의 인증을 처리합니다. 요청에서
JWT 토큰을 추출하고 이를 검증하여 인증 정보를 설정합니다.

## 필드 설명

- **`AUTHORIZATION_HEADER`**
    - **설명**: HTTP 요청의 Authorization 헤더 이름.
    - **타입**: `String`
    - **값**: `"Authorization"`

- **`BEARER_PREFIX`**
    - **설명**: JWT 토큰의 접두사.
    - **타입**: `String`
    - **값**: `"Bearer "`

- **`COOKIE_NAME`**
    - **설명**: JWT 토큰을 저장할 쿠키의 이름.
    - **타입**: `String`
    - **값**: `"JWT"`

- **`jwtTokenProvider`**
    - **설명**: JWT 토큰을 생성하고 검증하는 서비스.
    - **타입**: `JwtTokenProvider`

## 메서드 설명

- **`doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)`**
    - **설명**: 요청을 필터링하여 인증을 수행하고, 인증 정보를 SecurityContext에 설정합니다.
    - **매개변수**:
        - `request`: HTTP 요청
        - `response`: HTTP 응답
        - `chain`: 필터 체인
    - **예외**:
        - `IOException`
        - `ServletException`

- **`getAuthentication(HttpServletRequest request)`**
    - **설명**: 요청에서 JWT 토큰을 추출하고 인증 객체를 반환합니다.
    - **매개변수**:
        - `request`: HTTP 요청
    - **반환값**: `Authentication` 객체 또는 `null`

- **`extractTokenFromHeader(HttpServletRequest request)`**
    - **설명**: Authorization 헤더에서 JWT 토큰을 추출합니다.
    - **매개변수**:
        - `request`: HTTP 요청
    - **반환값**: `Optional<String>`

- **`extractTokenFromCookie(HttpServletRequest request)`**
    - **설명**: 쿠키에서 JWT 토큰을 추출합니다.
    - **매개변수**:
        - `request`: HTTP 요청
    - **반환값**: `Optional<String>`

- **`handleJwtException(HttpServletResponse response, JwtException exception)`**
    - **설명**: JWT 관련 예외를 처리하고 401 Unauthorized 응답을 반환합니다.
    - **매개변수**:
        - `response`: HTTP 응답
        - `exception`: JWT 예외
    - **예외**:
        - `IOException`

## 클래스 설명

- **`@Component`**: Spring의 컴포넌트 스캔에 의해 관리되는 빈.
- **`@RequiredArgsConstructor`**: Lombok 어노테이션으로 final 필드에 대한 생성자를 자동으로 생성.
- **`@Slf4j`**: Lombok 어노테이션으로 로깅을 위한 `Logger` 객체 생성.
- **`UsernamePasswordAuthenticationFilter`**: 기본적인 사용자 이름과 비밀번호 기반 인증 필터를 확장하여 JWT 인증을 처리.

`JwtAuthenticationFilter`는 JWT 토큰을 기반으로 인증을 처리하며, 토큰의 유효성을 검사하고 인증 정보를 설정합니다.

# JwtTokenProvider 클래스

`JwtTokenProvider` 클래스는 JWT 토큰의 생성과 검증을 담당하는 서비스입니다. JWT 기반 인증을 위해 `JwtTokenService` 인터페이스를 구현합니다.

## 필드 설명

- **`redisService`**
    - **설명**: Redis에 대한 접근을 제공하는 서비스. 토큰의 유효성을 검사하는 데 사용.
    - **타입**: `RedisService`

- **`jwtTokenProperties`**
    - **설명**: JWT 설정을 관리하는 프로퍼티 클래스.
    - **타입**: `JwtTokenProperties`

- **`secretKey`**
    - **설명**: JWT 서명을 위한 비밀 키. `Keys.hmacShaKeyFor()`로 초기화.
    - **타입**: `SecretKey`

## 생성자

- **`JwtTokenProvider(RedisService redisService, JwtTokenProperties jwtTokenProperties)`**
    - **설명**: `RedisService`와 `JwtTokenProperties`를 주입받아 `secretKey`를 초기화.

## 메서드 설명

- **`createToken(String username)`**
    - **설명**: 주어진 사용자 이름으로 JWT 엑세스 토큰을 생성합니다.
    - **매개변수**:
        - `username`: JWT에 포함될 사용자 이름
    - **반환값**: JWT 엑세스 토큰 (`String`)

- **`createRefreshToken(String username)`**
    - **설명**: 주어진 사용자 이름으로 JWT 리프레시 토큰을 생성합니다.
    - **매개변수**:
        - `username`: JWT에 포함될 사용자 이름
    - **반환값**: JWT 리프레시 토큰 (`String`)

- **`refreshToken(String accessToken)`**
    - **설명**: JWT 엑세스 토큰으로부터 리프레시 토큰을 재발급합니다.
    - **매개변수**:
        - `accessToken`: 유효한 JWT 엑세스 토큰
    - **반환값**: 새로 생성된 JWT 리프레시 토큰 (`String`)
    - **예외**: `CustomException` (엑세스 토큰이 유효하지 않은 경우)

- **`validateToken(String token)`**
    - **설명**: JWT 토큰의 유효성을 검사합니다. 서명을 검증하고 Redis에서 토큰의 존재 여부를 확인합니다.
    - **매개변수**:
        - `token`: JWT 토큰
    - **반환값**: 유효한 경우 `true`, 그렇지 않으면 `false`
    - **예외**: `CustomException` (유효하지 않은 토큰일 경우)

- **`getUsername(String token)`**
    - **설명**: JWT 토큰에서 사용자 이름을 추출합니다.
    - **매개변수**:
        - `token`: JWT 토큰
    - **반환값**: 사용자 이름 (`String`)
    - **예외**: `CustomException` (유효하지 않은 토큰일 경우)

- **`getAuthentication(String token)`**
    - **설명**: JWT 토큰에서 인증 객체를 생성합니다.
    - **매개변수**:
        - `token`: JWT 토큰
    - **반환값**: 인증 객체 (`Authentication`)

## 클래스 설명

- **`@Component`**: Spring의 컴포넌트 스캔에 의해 관리되는 빈.
- **`@RequiredArgsConstructor`**: Lombok 어노테이션으로 final 필드에 대한 생성자를 자동으로 생성.
- **`JwtTokenService`**: JWT 관련 기능을 제공하는 서비스 인터페이스 구현.

`JwtTokenProvider`는 JWT 토큰의 생성, 검증, 사용자 정보 추출 및 인증 객체 생성을 담당하며, JWT 기반 인증을 효과적으로 수행합니다.
