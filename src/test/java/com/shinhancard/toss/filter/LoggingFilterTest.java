package com.shinhancard.toss.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhancard.toss.properties.LoggingProperties;
import com.shinhancard.toss.service.LogService;
import com.shinhancard.toss.wrapper.WrappedHttpServletRequest;
import com.shinhancard.toss.wrapper.WrappedHttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * {@link LoggingFilter} 클래스의 단위 테스트를 수행하는 클래스입니다.
 * <p>
 * 이 클래스는 요청 및 응답 로그 기록 필터의 다양한 동작을 검증하기 위해 작성된 테스트 케이스를 포함하고 있습니다.
 * </p>
 */
public class LoggingFilterTest {

	@Mock
	private LoggingProperties loggingProperties; // Mock LoggingProperties 인스턴스

	@Mock
	private LogService logService; // Mock LogService 인스턴스

	@InjectMocks
	private LoggingFilter loggingFilter; // LoggingFilter 인스턴스

	/**
	 * 테스트 실행 전에 Mock 객체 및 필드 설정을 수행합니다.
	 */
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this); // Mock 객체 초기화
		ObjectMapper objectMapper = new ObjectMapper();
		// ObjectMapper를 LoggingFilter 인스턴스에 주입
		ReflectionTestUtils.setField(loggingFilter, "objectMapper", objectMapper);
	}

	/**
	 * 필터 내부 로직이 정상적으로 동작하는지 테스트합니다.
	 *
	 * @throws Exception 필터 처리 중 발생할 수 있는 예외
	 */
	@Test
	@DisplayName("필터 내부 로직을 테스트합니다.")
	public void testDoFilterInternal() throws Exception {
		// Mock 객체 설정
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);
		WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(response);

		// Mock 요청 설정
		when(request.getMethod()).thenReturn("GET"); // HTTP 메서드
		when(request.getRequestURI()).thenReturn("/test-uri"); // 요청 URI
		when(request.getQueryString()).thenReturn("param=value"); // 쿼리 문자열
		when(request.getRemoteAddr()).thenReturn("127.0.0.1"); // 원격 주소
		when(request.getHeaderNames()).thenReturn(mockEnumeration("Header1", "Header2")); // 헤더 이름 설정

		// Mock 프로퍼티 설정
		when(loggingProperties.getRequestBody().isTruncate()).thenReturn(false); // 요청 본문 자르기 비활성화
		when(loggingProperties.getResponseBody().isTruncate()).thenReturn(false); // 응답 본문 자르기 비활성화

		// 필터 메서드 호출
		loggingFilter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);

		// 검증
		verify(filterChain).doFilter(wrappedRequest, wrappedResponse); // 필터 체인 호출 검증
		verify(logService).sendLog(anyString()); // 로그 전송 검증
	}

	/**
	 * 본문이 최대 크기를 초과할 때 본문이 잘리는지 테스트합니다.
	 */
	@Test
	@DisplayName("본문이 최대 크기를 초과할 때 본문이 잘리는지 테스트합니다.")
	public void testTruncateBody() {
		// Mock 프로퍼티 설정
		when(loggingProperties.getBody().getMaxSize()).thenReturn(10); // 최대 본문 크기 10

		String body = "This is a very long body that needs to be truncated."; // 긴 본문
		String truncatedBody = loggingFilter.truncateBody(body); // 본문 자르기 메서드 호출

		// 검증
		assertEquals("This is a v... [TRUNCATED]", truncatedBody); // 자른 본문 검증
	}

	/**
	 * 응답 본문이 빈 문자열일 때 로그가 올바르게 기록되는지 테스트합니다.
	 *
	 * @throws IOException 본문 처리 중 발생할 수 있는 예외
	 */
	@Test
	@DisplayName("응답 본문이 빈 문자열일 때 로그가 올바르게 기록되는지 테스트합니다.")
	public void testLogResponseWithEmptyBody() throws IOException {
		// Mock 객체 설정
		WrappedHttpServletResponse response = mock(WrappedHttpServletResponse.class);

		// Mock 응답 설정
		when(response.getStatus()).thenReturn(200); // 응답 상태 코드
		when(response.getBody()).thenReturn(""); // 빈 본문
		when(response.getHeaderNames()).thenReturn(Collections.emptyList()); // 빈 Collection 설정

		// Mock 프로퍼티 설정
		when(loggingProperties.getResponseBody().isTruncate()).thenReturn(false); // 응답 본문 자르기 비활성화

		loggingFilter.logResponse(response); // 응답 로그 기록 메서드 호출

		// 검증
		verify(logService).sendLog(anyString()); // 로그 전송 검증
	}

	/**
	 * 요청 헤더가 여러 개인 경우 로그가 올바르게 기록되는지 테스트합니다.
	 *
	 * @throws IOException 헤더 처리 중 발생할 수 있는 예외
	 */
	@Test
	@DisplayName("요청 헤더가 여러 개인 경우 로그가 올바르게 기록되는지 테스트합니다.")
	public void testLogRequestWithMultipleHeaders() throws IOException {
		// Mock 객체 설정
		WrappedHttpServletRequest request = mock(WrappedHttpServletRequest.class);

		// Mock 요청 설정
		when(request.getMethod()).thenReturn("POST"); // HTTP 메서드
		when(request.getRequestURI()).thenReturn("/api/test"); // 요청 URI
		when(request.getQueryString()).thenReturn("query=value"); // 쿼리 문자열
		when(request.getRemoteAddr()).thenReturn("192.168.1.1"); // 원격 주소
		when(request.getHeaderNames()).thenReturn(mockEnumeration("Header1", "Header2")); // 헤더 이름 설정
		when(request.getHeader("Header1")).thenReturn("Value1"); // 첫 번째 헤더 값
		when(request.getHeader("Header2")).thenReturn("Value2"); // 두 번째 헤더 값

		// Mock 프로퍼티 설정
		when(loggingProperties.getRequestBody().isTruncate()).thenReturn(false); // 요청 본문 자르기 비활성화

		loggingFilter.logRequest(request); // 요청 로그 기록 메서드 호출

		// 검증
		verify(logService).sendLog(anyString()); // 로그 전송 검증
	}

	/**
	 * 요청 본문이 없을 때 로그가 올바르게 기록되는지 테스트합니다.
	 *
	 * @throws IOException 본문 처리 중 발생할 수 있는 예외
	 */
	@Test
	@DisplayName("요청 본문이 없을 때 로그가 올바르게 기록되는지 테스트합니다.")
	public void testLogRequestWithNoBody() throws IOException {
		// Mock 객체 설정
		WrappedHttpServletRequest request = mock(WrappedHttpServletRequest.class);

		// Mock 요청 설정
		when(request.getMethod()).thenReturn("GET"); // HTTP 메서드
		when(request.getRequestURI()).thenReturn("/api/test"); // 요청 URI
		when(request.getQueryString()).thenReturn("query=value"); // 쿼리 문자열
		when(request.getRemoteAddr()).thenReturn("192.168.1.1"); // 원격 주소
		when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration()); // 빈 헤더 설정
		when(request.getBody()).thenReturn(""); // 빈 본문

		// Mock 프로퍼티 설정
		when(loggingProperties.getRequestBody().isTruncate()).thenReturn(false); // 요청 본문 자르기 비활성화

		loggingFilter.logRequest(request); // 요청 로그 기록 메서드 호출

		// 검증
		verify(logService).sendLog(anyString()); // 로그 전송 검증
	}

	/**
	 * 응답 상태 코드가 500인 경우 필터가 올바르게 동작하는지 테스트합니다.
	 *
	 * @throws Exception 필터 처리 중 발생할 수 있는 예외
	 */
	@Test
	@DisplayName("응답 상태 코드가 500인 경우 필터가 올바르게 동작하는지 테스트합니다.")
	public void testFilterHandlesServerError() throws Exception {
		// Mock 객체 설정
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);
		WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(response);

		// Mock 요청 및 응답 설정
		when(request.getMethod()).thenReturn("GET"); // HTTP 메서드
		when(request.getRequestURI()).thenReturn("/test-uri"); // 요청 URI
		when(request.getQueryString()).thenReturn("param=value"); // 쿼리 문자열
		when(request.getRemoteAddr()).thenReturn("127.0.0.1"); // 원격 주소
		when(response.getStatus()).thenReturn(500); // 서버 오류 상태 코드

		// 필터 메서드 호출 시 예외 발생 설정
		doThrow(new IOException("Simulated exception")).when(filterChain).doFilter(wrappedRequest, wrappedResponse);

		// 예외 처리
		try {
			loggingFilter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);
		} catch (IOException e) {
			// 예외 발생 검증
			assertEquals("Simulated exception", e.getMessage());
		}

		// 검증
		verify(logService).sendLog(anyString()); // 로그 전송 검증
	}

	/**
	 * Enumeration을 생성하는 유틸리티 메서드입니다.
	 *
	 * @param values 헤더 이름들
	 * @return Enumeration<String> 객체
	 */
	private Enumeration<String> mockEnumeration(String... values) {
		return Collections.enumeration(Arrays.asList(values));
	}
}
