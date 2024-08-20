# CookieUtil 클래스

`CookieUtil` 클래스는 쿠키의 생성, 업데이트, 삭제 및 조회를 위한 유틸리티 메서드를 제공합니다. 쿠키에 보안 설정을 적용할 수 있습니다.

## 메서드

- **`createCookie(String name, String value)`**
    - **설명**: 기본 만료 시간으로 쿠키를 생성합니다.
    - **반환값**: 설정된 쿠키 객체

- **`createCookie(String name, String value, int maxAge)`**
    - **설명**: 주어진 만료 시간으로 쿠키를 생성합니다.
    - **매개변수**:
        - `name`: 쿠키 이름
        - `value`: 쿠키 값
        - `maxAge`: 만료 시간 (초 단위)
    - **반환값**: 설정된 쿠키 객체

- **`deleteCookie(HttpServletResponse response, String name)`**
    - **설명**: 쿠키를 삭제합니다.
    - **매개변수**:
        - `response`: HTTP 응답 객체
        - `name`: 삭제할 쿠키 이름

- **`updateCookie(HttpServletResponse response, String name, String newValue)`**
    - **설명**: 쿠키 값을 업데이트합니다.
    - **매개변수**:
        - `response`: HTTP 응답 객체
        - `name`: 쿠키 이름
        - `newValue`: 새로운 쿠키 값

- **`getCookie(Cookie[] cookies, String name)`**
    - **설명**: 요청에서 특정 쿠키를 찾습니다.
    - **매개변수**:
        - `cookies`: 요청 쿠키 배열
        - `name`: 찾을 쿠키 이름
    - **반환값**: `Optional`로 감싼 쿠키

## 보안 설정

- **`configureCookie(Cookie cookie, int maxAge)`**
    - **설명**: 쿠키에 보안 속성을 적용합니다.
    - **매개변수**:
        - `cookie`: 설정할 쿠키
        - `maxAge`: 만료 시간 (초 단위)
    - **설정 사항**:
        - `setHttpOnly(true)`: JavaScript 접근 차단
        - `setPath("/")`: 모든 경로에서 유효
        - `setMaxAge(maxAge)`: 만료 시간 설정
        - `setSecure(true)`: HTTPS 전송만 허용

## 클래스 설명

- **`@UtilityClass`**: Lombok 어노테이션으로, 모든 메서드가 `static`인 유틸리티 클래스로 선언됩니다.

## 보안 고려사항

- **HTTPS 사용**: `setSecure(true)`는 HTTPS에서만 사용합니다. HTTP에서는 설정을 변경하거나 제거해야 합니다.
- **HTTPOnly 속성**: `setHttpOnly(true)`는 XSS 공격 방지를 위한 설정입니다.

쿠키 관리와 관련된 공통 작업을 처리하며, 보안 속성을 적용하여 안전한 쿠키 사용을 지원합니다.
