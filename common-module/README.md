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
