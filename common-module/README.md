# Shinhancard Common Wrapper 모듈

`shinhancard.common.wrapper` 모듈은 Spring Boot 애플리케이션에서 HTTP 요청 및 응답 본문을 캐싱하고 래핑하는 유틸리티 클래스를 제공합니다. 이 유틸리티는 요청 및 응답 데이터를
여러 번 읽거나 수정할 수 있도록 도와줍니다.

## 기능

- **요청 본문 캐싱**: 요청 본문을 캐싱하여 여러 번 읽을 수 있도록 합니다.
- **응답 본문 캐싱**: 응답 본문을 캐싱하고 클라이언트에게 전송하기 전에 여러 번 읽거나 수정할 수 있습니다.

## 클래스

### `WrappedHttpServletRequest`

`HttpServletRequest`를 래핑하여 요청 본문을 캐싱하고 여러 번 읽을 수 있도록 합니다.

#### 생성자

- `WrappedHttpServletRequest(HttpServletRequest request) throws IOException`:
  제공된 `HttpServletRequest`로부터 요청 본문을 읽고 캐싱하여 새로운 `WrappedHttpServletRequest`를 생성합니다.

#### 메서드

- `ServletInputStream getInputStream()`: 캐시된 요청 본문을 읽기 위한 `ServletInputStream`을 반환합니다.
- `String getBody()`: 캐시된 요청 본문을 `String`으로 반환합니다.

### `WrappedHttpServletResponse`

`HttpServletResponse`를 래핑하여 응답 본문을 캐싱하고 클라이언트에게 전송하기 전에 여러 번 읽거나 수정할 수 있도록 합니다.

#### 생성자

- `WrappedHttpServletResponse(HttpServletResponse response)`:
  내부 버퍼를 사용하여 응답 본문을 캐싱하는 새로운 `WrappedHttpServletResponse`를 생성합니다.

#### 메서드

- `ServletOutputStream getOutputStream()`: 내부 버퍼에 쓰기 위한 `ServletOutputStream`을 반환합니다.
- `void flushBuffer() throws IOException`: 캐시된 응답 본문을 원래 응답의 출력 스트림에 기록하고 플러시합니다.
- `void reset()`: 응답을 리셋하고 버퍼를 비웁니다.
- `void resetBuffer()`: 응답을 리셋하지 않고 버퍼만 비웁니다.
- `String getBody()`: 캐시된 응답 본문을 `String`으로 반환합니다.

## 사용 방법

### 요청 캐싱 예제

요청 본문을 캐싱하기 위해 `WrappedHttpServletRequest`를 필터나 인터셉터에서 래핑하여 사용할 수 있습니다:

```java
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class RequestCachingFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest((HttpServletRequest)request);
		chain.doFilter(wrappedRequest, response);
	}
}
```

### 응답 캐싱 예제

응답 본문을 캐싱하기 위해 `WrappedHttpServletResponse`를 필터나 인터셉터에서 래핑하여 사용할 수 있습니다:

```java
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseCachingFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse((HttpServletResponse)response);
		chain.doFilter(request, wrappedResponse);
		// 응답 본문을 사용할 수 있음
		String responseBody = wrappedResponse.getBody();
		// 필요에 따라 응답 본문을 수정하거나 로그를 기록할 수 있음
	}
}
```

## JsonProcessingException

- JsonProcessingException 클래스는 JSON 처리 중 발생하는 예외를 나타내는 커스텀 예외 클래스입니다.

### 생성자

- JsonProcessingException(ResponseCode responseCode)
- responseCode: 응답 코드(enum 타입, ResponseCode 사용)

### 메서드

- getHttpStatus(): HTTP 상태 코드를 반환합니다.
- getErrorCode(): 오류 코드를 반환합니다.
- getErrorMessage(): 오류 메시지를 반환합니다.

```java
try{
	// JSON 처리 코드
	}catch(JsonProcessingException e){
HttpStatus status = e.getHttpStatus();
String errorCode = e.getErrorCode();
String errorMessage = e.getErrorMessage();
// 예외 처리 코드
}
```

## ResponseCode

- ResponseCode 열거형은 응답 코드와 관련된 정보를 정의합니다. 각 응답 코드는 4자리 숫자 코드, 설명 메시지, HTTP 상태 코드를 포함합니다.

### 열거형 상수

- SUCCESS: 요청이 성공적으로 처리된 경우
- CORS_ORIGIN_POLICY_VIOLATION: CORS 출처 정책 위반
- CORS_METHOD_POLICY_VIOLATION: CORS 메서드 정책 위반
- CORS_HEADERS_POLICY_VIOLATION: CORS 헤더 정책 위반
- XSS_DETECTED: XSS 공격 감지
- XSS_IN_PARAMETER: 요청 파라미터에서 XSS 공격 감지
- XSS_IN_COOKIE: 쿠키에서 XSS 공격 감지
- XSS_IN_BODY: 요청 본문에서 XSS 공격 감지
- SQL_INJECTION_PARAMETER_DETECTED: 파라미터에서 SQL 인젝션 감지
- SQL_INJECTION_BODY_DETECTED: 요청 본문에서 SQL 인젝션 감지
- SQL_INJECTION_COOKIE_DETECTED: 쿠키에서 SQL 인젝션 감지
- JSON_PROCESSING_ERROR: JSON 처리 중 오류 발생

### 사용 예시

```java
ResponseCode responseCode = ResponseCode.SUCCESS;
HttpStatus httpStatus = responseCode.getHttpStatus();
String code = responseCode.getCode();
String message = responseCode.getMessage();
```

## ResponseVo

- ResponseVo 클래스는 응답 데이터를 표현하는 불변 객체입니다. 이 클래스는 성공 및 실패 응답을 포맷하는 데 사용됩니다.

### 생성자

- ResponseVo(DataHeader dataHeader, T dataBody)

### 메서드

- success(T data, ResponseCode responseCode, Optional<String> traceId): 성공 응답을 생성합니다.
- error(ResponseCode responseCode, Optional<String> traceId): 에러 응답을 생성합니다.
- generateTraceId(): traceId를 생성합니다. (기본적으로 UUID 기반)
- toString(): JSON 형식의 문자열을 반환합니다.

### DataHeader

- DataHeader 레코드는 응답의 헤더 정보를 담고 있습니다.

- resultCode: 결과 코드
- resultMessage: 결과 메시지
- traceId: trace ID

### 사용 예시

```java
// 성공 응답 생성
ResponseVo<String> successResponse = ResponseVo.success(
	"데이터 내용",
	ResponseCode.SUCCESS,
	Optional.of("trace-id-12345")
);

// 에러 응답 생성
ResponseVo<Void> errorResponse = ResponseVo.error(
	ResponseCode.JSON_PROCESSING_ERROR,
	Optional.empty()
);

// JSON 변환
String jsonResponse = successResponse.toString();
```

### 입출력 예시

#### 성공 응답

- 입력:

```java
ResponseVo.success("성공적인 데이터",ResponseCode.SUCCESS, Optional.of("trace-id-12345"));
```

- 출력:

```json
{
  "dataHeader": {
    "resultCode": "0000",
    "resultMessage": "요청이 성공적으로 처리되었습니다.",
    "traceId": "trace-id-12345"
  },
  "dataBody": "성공적인 데이터"
}
```

#### 에러 응답

- 입력:

```java
ResponseVo.error(ResponseCode.JSON_PROCESSING_ERROR, Optional.empty());
```

- 출력:

```json
{
  "dataHeader": {
    "resultCode": "4001",
    "resultMessage": "JSON 처리 중 오류가 발생했습니다.",
    "traceId": "생성된 traceId"
  },
  "dataBody": null
}
```