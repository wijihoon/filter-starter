# JwtRequest 클래스

`JwtRequest` 클래스는 JWT 인증 요청을 위한 데이터 전송 객체 (DTO)입니다.

## 필드

- **`grantType`**: 인증 요청의 그랜트 타입. 고정값 `"authorization_code"`. 필수 입력 항목.
    - **유효성 검증**: `@NotBlank(message = "그랜트 타입은 비어 있을 수 없습니다")`

- **`clientId`**: 애플리케이션의 REST API 키. 필수 입력 항목.
    - **유효성 검증**: `@NotBlank(message = "클라이언트 ID는 비어 있을 수 없습니다")`

- **`redirectUrl`**: 인가 코드가 리다이렉트된 URL. 필수 입력 항목.
    - **유효성 검증**: `@NotBlank(message = "리다이렉트 URL은 비어 있을 수 없습니다")`

- **`code`**: 인가 코드. 필수 입력 항목.
    - **유효성 검증**: `@NotBlank(message = "인가 코드는 비어 있을 수 없습니다")`

- **`clientSecret`**: 보안 강화용 추가 코드. 선택적 필드.
    - **유효성 검증**: 없음

## 클래스 설명

- **불변 객체**: `record` 키워드로 정의된 불변 객체입니다.

---

# JwtResponse 클래스

`JwtResponse` 클래스는 JWT 응답 정보를 담기 위한 데이터 전송 객체 (DTO)입니다.

## 필드

- **`tokenType`**: 토큰의 유형. 예: "bearer".
- **`accessToken`**: 액세스 토큰.
- **`expiresIn`**: 액세스 토큰의 만료 시간 (초).
- **`refreshToken`**: 리프레시 토큰.
- **`refreshTokenExpiresIn`**: 리프레시 토큰의 만료 시간 (초).
- **`scope`**: 토큰의 권한 범위. 예: "account_email profile".

## 클래스 설명

- **불변 객체**: `record` 키워드로 정의된 불변 객체입니다.

---

# ResponseVo 클래스

`ResponseVo` 클래스는 서버의 응답 데이터를 표현하는 불변 객체입니다.

## 필드

- **`success`**: 요청의 성공 여부 (`true` 또는 `false`).
- **`message`**: 응답 메시지.
- **`data`**: 응답 데이터 (제네릭 `<T>`).
- **`status`**: HTTP 상태 코드 (`HttpStatus`).

## 생성 메서드

- **`success(T data)`**: 성공적인 응답을 생성. HTTP 상태 코드는 200 OK.
- **`error(String message, HttpStatus status)`**: 실패 응답을 생성. 오류 메시지와 HTTP 상태 코드 포함.
- **`successWithHeader(T data)`**: 성공적인 응답 생성 (헤더 처리 로직 없음).

## 클래스 설명

- **불변 객체**: `record` 키워드로 정의된 불변 객체입니다.
- **제네릭 타입**: 다양한 타입의 응답 데이터를 처리할 수 있도록 설계되었습니다.
