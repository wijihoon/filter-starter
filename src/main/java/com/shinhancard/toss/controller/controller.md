# AuthController

`AuthController` 클래스는 인증 관련 요청을 처리하는 REST 컨트롤러입니다. 이 클래스는 사용자의 인증을 수행하고, JWT 토큰을 다양한 방식으로 반환합니다. 주로 `AuthService`를 사용하여
인증을 처리하며, 응답을 다양한 방식으로 반환합니다.

## 클래스 설명

`AuthController`는 다음과 같은 인증 관련 요청을 처리합니다:

- JWT 토큰을 쿠키에 저장하여 리디렉션 처리
- JWT 토큰을 쿠키에 저장하여 반환
- JWT 토큰을 응답 헤더에 저장하여 반환
- JWT 토큰을 응답 본문에 저장하여 반환

## 주요 메소드

### `authenticateUserAndRedirect`

- **설명**: 사용자를 인증하고, JWT 토큰을 쿠키에 저장한 후, 지정된 URL로 리디렉션합니다.
- **HTTP 메소드**: `POST`
- **요청 URL**: `/api/login-and-redirect`
- **요청 바디**:
  ```json
  {
    "encryptedCi": "encrypted_ci_example",
    "redirectUrl": "http://example.com/redirect"
  }
- *응답*:
- **성공**: 인증 성공 후 지정된 URL로 리디렉션하며, ResponseVo.success(null) 반환
- **잘못된 요청**: 리디렉션 URL이 유효하지 않거나 누락된 경우, 400 Bad Request 응답
- **인증 실패**: 401 Unauthorized 응답
- **예외 처리**: CustomException 발생 시, 로그에 오류를 기록하고 401 Unauthorized 응답 반환

## `authenticateUserWithCookie`

- **설명**: 사용자를 인증하고, JWT 토큰을 쿠키에 저장하여 반환합니다. 이 메소드는 인증을 수행한 후, JWT 토큰을 클라이언트의 쿠키에 저장하여 반환하며, 클라이언트는 이후 요청에서 이 쿠키를 사용하여
  인증을 유지할 수 있습니다.

- **HTTP 메소드**: `POST`

- **요청 URL**: `/api/login-with-cookie`

- **요청 바디**:
  ```json
  {
    "encryptedCi": "encrypted_ci_example"
  }

- **응답**:
- **성공**: 인증 성공 시, ResponseVo.success("Authentication successful") 반환
- **인증 실패**: 401 Unauthorized 응답
- **예외 처리**: CustomException 발생 시, 로그에 오류를 기록하고 401 Unauthorized 응답 반환

## `authenticateUserWithHeader`

- **설명**: 사용자를 인증하고, JWT 토큰을 응답 헤더에 저장하여 반환합니다. 이 메소드는 인증이 성공하면 JWT 토큰을 `Authorization` 헤더에 포함하여 클라이언트에 반환합니다. 클라이언트는 이후
  요청에서 이 헤더를 사용하여 인증을 유지할 수 있습니다.

- **HTTP 메소드**: `POST`

- **요청 URL**: `/api/login-with-header`

- **요청 바디**:
  ```json
  {
    "encryptedCi": "encrypted_ci_example"
  }
- **응답**:

- **성공**: 인증 성공 시, ResponseVo.success("Authentication successful") 반환 및 Authorization 헤더에 JWT 토큰 설정
- **인증 실패**: 401 Unauthorized 응답
- **예외 처리**: CustomException 발생 시, 로그에 오류를 기록하고 401 Unauthorized 응답 반환

## `authenticateUserWithHeader`

- **설명**: 사용자를 인증하고, JWT 토큰을 응답 헤더에 저장하여 반환합니다. 인증이 성공하면 JWT 토큰이 `Authorization` 헤더에 포함되어 클라이언트에 반환됩니다. 클라이언트는 이후 요청에서 이
  헤더를 사용하여 인증을 유지할 수 있습니다.

- **HTTP 메소드**: `POST`

- **요청 URL**: `/api/login-with-header`

- **요청 바디**:
  ```json
  {
    "encryptedCi": "encrypted_ci_example"
  }

- **응답**:

- **성공**: 인증 성공 시, ResponseVo.success("Authentication successful") 반환 및 Authorization 헤더에 JWT 토큰 설정
- **인증 실패**: 401 Unauthorized 응답
- **예외 처리**: CustomException 발생 시, 로그에 오류를 기록하고 401 Unauthorized 응답 반환

## `authenticateUserWithBody`

- **설명**: 사용자를 인증하고, JWT 토큰을 응답 본문에 저장하여 반환합니다. 인증이 성공하면 `accessToken`과 `refreshToken`이 포함된 JWT 토큰 객체를 응답 본문으로 클라이언트에
  반환합니다.

- **HTTP 메소드**: `POST`

- **요청 URL**: `/api/login-with-body`

- **요청 바디**:
  ```json
  {
    "encryptedCi": "encrypted_ci_example"
  }

- **응답**:

- **성공**:
  ```json
    {
        "data": {
        "accessToken": "jwt_token_example",
        "refreshToken": "refresh_token_example"
        }
    }   
- **인증 실패**: 401 Unauthorized 응답
- **예외 처리**: CustomException 발생 시, 로그에 오류를 기록하고 401 Unauthorized 응답 반환

## `RedirectController`

리다이렉션 관련 요청을 처리하는 컨트롤러입니다. 이 컨트롤러는 쿠키에 담긴 JWT 토큰을 검증하고, 유효한 경우 특정 URL로 리다이렉션합니다.

### **클래스 설명**

- **설명**: 쿠키에 담긴 JWT 토큰을 검증하고, 유효한 경우 특정 URL로 리다이렉션합니다.
- **어노테이션**:
    - `@Controller`: 이 클래스가 Spring MVC의 컨트롤러임을 나타냅니다.
    - `@RequestMapping("/api")`: 이 컨트롤러의 모든 요청이 `/api` 경로로 시작됨을 나타냅니다.
    - `@RequiredArgsConstructor`: `final` 필드를 포함한 생성자를 자동으로 생성합니다.
    - `@Tag(name = "Redirect", description = "리다이렉션 관련 API")`: Swagger UI에서의 태그와 설명을 제공합니다.

### **메소드**

#### `redirect`

- **설명**: 쿠키에 담긴 JWT 토큰을 검증하고 유효할 경우 지정된 URL로 리다이렉션합니다.
- **HTTP 메소드**: `GET`
- **요청 URL**: `/api/redirect`
- **파라미터**:
    - `HttpServletRequest request`: HTTP 요청 객체
    - `HttpServletResponse response`: HTTP 응답 객체
- **예외**:
    - `IOException`: 입출력 예외
- **동작 과정**:
    1. `authService.getTokenFromCookie(request)`를 호출하여 쿠키에서 JWT 토큰을 추출합니다.
    2. 토큰이 `null`이거나 `authService.validateToken(token)`을 통해 유효하지 않은 경우, `401 Unauthorized` 응답을 보냅니다.
    3. 토큰이 유효한 경우, `response.sendRedirect("https://other-domain.com/success")`를 호출하여 특정 URL로 리다이렉션합니다.

- **Swagger UI 설명**:
    - `summary`: 토큰 검증 및 리다이렉션
    - `description`: 쿠키에 담긴 토큰을 검증하고 유효할 경우 리다이렉션합니다.
    - `responses`:
        - `302`: 리다이렉션 성공
        - `401`: 유효하지 않은 토큰
