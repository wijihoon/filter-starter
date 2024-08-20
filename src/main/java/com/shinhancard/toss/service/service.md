# AuthServiceImpl 클래스

`AuthServiceImpl` 클래스는 JWT 기반 인증을 수행하는 서비스입니다.

## 필드

- **`jwtTokenService`**: JWT 토큰 생성 및 검증 서비스 (`JwtTokenService`)
- **`redisService`**: Redis와의 상호작용 서비스 (`RedisService`)

## 메서드

- **`authenticate(String encryptedCi)`**: 암호화된 CI로 JWT 엑세스 토큰을 생성합니다. 실패 시 `CustomException` 발생.
- **`decryptCi(String encryptedCi)`**: 암호화된 CI를 Base64로 복호화합니다. 실패 시 `CustomException` 발생.
- **`isValidUser(String ci)`**: 복호화된 CI로 사용자 유효성을 검사합니다. 현재는 빈 문자열이 아닌 경우 `true` 반환.
- **`createToken(String username)`**: 주어진 사용자 이름으로 JWT 엑세스 토큰을 생성합니다.
- **`getTokenFromCookie(HttpServletRequest request)`**: 요청에서 JWT 쿠키를 추출합니다. 쿠키가 없으면 `CustomException` 발생.
- **`findJwtCookie(Cookie[] cookies)`**: 쿠키 배열에서 JWT 쿠키를 찾습니다.
- **`validateToken(String token)`**: JWT 토큰의 유효성을 검사합니다. 유효하지 않으면 `false` 반환.
- **`authenticateWithTokens(JwtRequest jwtRequest)`**: JWT 요청 객체로 사용자 인증을 수행하고 JWT 토큰을 생성하여 반환합니다.

## 어노테이션

- **`@Service`**: Spring의 서비스 컴포넌트로 등록.
- **`@RequiredArgsConstructor`**: Lombok으로 `final` 필드 생성자 자동 생성.
- **`@Slf4j`**: Lombok으로 로깅을 위한 `Logger` 자동 생성.

---

# RedisServiceImpl 클래스

`RedisServiceImpl` 클래스는 Redis 데이터 저장 및 조회를 처리합니다.

## 필드

- **`redisTemplate`**: Redis와 상호작용하는 `RedisTemplate<String, String>` 객체.

## 메서드

- **`save(String key, String value)`**: Redis에 데이터를 저장합니다.
- **`get(String key)`**: Redis에서 데이터를 조회합니다. 키가 없으면 `null` 반환.
- **`delete(String key)`**: Redis에서 데이터를 삭제합니다.

## 어노테이션

- **`@Service`**: Spring의 서비스 컴포넌트로 등록.
- **`@RequiredArgsConstructor`**: Lombok으로 `final` 필드 생성자 자동 생성.

## 예외 처리

예외 발생 시 `RuntimeException`을 던지며, 필요 시 로깅 기능 추가 가능.
