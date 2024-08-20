# LoggingFilter 클래스

`LoggingFilter`는 HTTP 요청과 응답을 로깅하는 필터입니다. `OncePerRequestFilter`를 확장하여 요청 및 응답 본문을 로그로 기록합니다.

## 주요 기능

- **요청/응답 래핑**: 본문을 읽을 수 있도록 요청과 응답을 래핑합니다.
- **로그 기록**: 메소드, URI, 쿼리 문자열, 헤더, 본문을 DEBUG 레벨로 기록합니다.
- **예외 처리**: 요청 처리 중 예외를 로그하고, 예외를 필터 체인에서 처리하도록 전달합니다.
- **MDC 관리**: 요청/응답 컨텍스트를 로그에 설정하고 처리 후 제거하여 메모리 누수 방지합니다.

## 주요 메서드

- **`doFilterInternal`**: 요청과 응답을 래핑하고 로그를 기록합니다.
- **`logRequest`**: 요청 정보와 본문을 로그에 기록합니다.
- **`logResponse`**: 응답 상태와 본문을 로그에 기록합니다.

### 내부 클래스

- **`WrappedHttpServletRequest`**: 요청 본문을 읽기 위한 래퍼 클래스.
- **`WrappedHttpServletResponse`**: 응답 본문을 기록하기 위한 래퍼 클래스.

---

# XssFilter 클래스

`XssFilter`는 HTTP 요청 파라미터를 HTML로 인코딩하여 XSS 공격을 방지하는 필터입니다.

## 주요 기능

- **XSS 방지**: 요청 파라미터를 HTML로 인코딩합니다.
- **요청 래핑**: `HttpServletRequest`를 래핑하여 모든 파라미터를 HTML로 인코딩합니다.

## 주요 메서드

- **`doFilter`**: 요청을 `XssRequestWrapper`로 래핑하여 파라미터를 인코딩하고 필터 체인에 전달합니다.

### 내부 클래스

- **`XssRequestWrapper`**: 요청 파라미터 값을 HTML로 인코딩하는 래퍼 클래스.
