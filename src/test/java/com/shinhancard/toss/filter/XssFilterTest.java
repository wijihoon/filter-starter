package com.shinhancard.toss.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.util.HtmlUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * {@link XssFilter}의 테스트 클래스입니다.
 * <p>
 * 이 클래스는 XSS 필터링 기능을 검증하기 위한 다양한 테스트 케이스를 포함하고 있습니다.
 * </p>
 */
class XssFilterTest {

	/**
	 * 요청 파라미터가 null인 경우를 테스트합니다.
	 */
	@Test
	@DisplayName("Test Get Parameter - Null Value")
	void testGetParameterNull() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getParameter("param")).thenReturn(null); // 요청 파라미터가 null로 설정

		XssFilter.XssRequestWrapper wrapper = new XssFilter.XssRequestWrapper(request);

		String result = wrapper.getParameter("param");
		assertNull(result); // 결과가 null인지 확인
	}

	/**
	 * 요청 파라미터가 빈 문자열인 경우를 테스트합니다.
	 */
	@Test
	@DisplayName("Test Get Parameter - Empty String")
	void testGetParameterEmpty() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getParameter("param")).thenReturn(""); // 요청 파라미터가 빈 문자열로 설정

		XssFilter.XssRequestWrapper wrapper = new XssFilter.XssRequestWrapper(request);

		String result = wrapper.getParameter("param");
		assertEquals("", result); // 결과가 빈 문자열인지 확인
	}

	/**
	 * HTML 특수 문자가 포함된 요청 파라미터를 테스트합니다.
	 */
	@Test
	@DisplayName("Test Get Parameter - Special Characters")
	void testGetParameterSpecialChars() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getParameter("param")).thenReturn("<b>bold</b> & <i>italic</i>"); // HTML 특수 문자가 포함된 파라미터

		XssFilter.XssRequestWrapper wrapper = new XssFilter.XssRequestWrapper(request);

		String result = wrapper.getParameter("param");
		String expected = HtmlUtils.htmlEscape("<b>bold</b> & <i>italic</i>"); // HTML로 인코딩된 예상 결과
		assertEquals(expected, result); // 결과가 예상 값과 일치하는지 확인
	}

	/**
	 * HTML 특수 문자가 포함된 요청 파라미터 배열을 테스트합니다.
	 */
	@Test
	@DisplayName("Test Get Parameter Values - Special Characters")
	void testGetParameterValuesSpecialChars() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getParameterValues("param")).thenReturn(new String[] {
			"<b>bold</b> & <i>italic</i>",
			"<script>alert('XSS')</script>"
		}); // HTML 특수 문자가 포함된 파라미터 배열

		XssFilter.XssRequestWrapper wrapper = new XssFilter.XssRequestWrapper(request);

		String[] result = wrapper.getParameterValues("param");
		String[] expected = new String[] {
			HtmlUtils.htmlEscape("<b>bold</b> & <i>italic</i>"),
			HtmlUtils.htmlEscape("<script>alert('XSS')</script>")
		}; // HTML로 인코딩된 예상 결과 배열

		assertEquals(expected[0], result[0]); // 첫 번째 값 비교
		assertEquals(expected[1], result[1]); // 두 번째 값 비교
	}

	/**
	 * 요청 헤더가 null인 경우를 테스트합니다.
	 */
	@Test
	@DisplayName("Test Get Header - Null Value")
	void testGetHeaderNull() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getHeader("header")).thenReturn(null); // 요청 헤더가 null로 설정

		XssFilter.XssRequestWrapper wrapper = new XssFilter.XssRequestWrapper(request);

		String result = wrapper.getHeader("header");
		assertNull(result); // 결과가 null인지 확인
	}

	/**
	 * 복수의 요청 헤더 값을 처리하는 경우를 테스트합니다.
	 */
	@Test
	@DisplayName("Test Get Headers - Multiple Values")
	void testGetHeadersMultipleValues() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getHeaders("header")).thenReturn(java.util.Collections.enumeration(java.util.Arrays.asList(
			"<b>bold</b> & <i>italic</i>",
			"<img src='x' onerror='alert(1)'/>"
		))); // 복수의 헤더 값

		XssFilter.XssRequestWrapper wrapper = new XssFilter.XssRequestWrapper(request);

		java.util.Enumeration<String> result = wrapper.getHeaders("header");
		java.util.Map<String, String> expected = new java.util.HashMap<>();
		expected.put("header", HtmlUtils.htmlEscape("<b>bold</b> & <i>italic</i>"));
		expected.put("header", HtmlUtils.htmlEscape("<img src='x' onerror='alert(1)'/>")); // HTML로 인코딩된 예상 결과

		int index = 0;
		while (result.hasMoreElements()) {
			String headerValue = result.nextElement();
			assertEquals(expected.get("header"), headerValue); // 각 헤더 값 비교
			index++;
		}
		assertEquals(2, index); // 헤더 값 개수가 2개인지 확인
	}

	/**
	 * 요청 본문을 HTML로 인코딩하는 경우를 테스트합니다.
	 */
	@Test
	@DisplayName("Test Get Reader - HTML Encoding")
	void testGetReader() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getReader()).thenReturn(
			new BufferedReader(new StringReader("<script>alert('XSS')</script>\n"))); // HTML 특수 문자가 포함된 요청 본문

		XssFilter.XssRequestWrapper wrapper = new XssFilter.XssRequestWrapper(request);

		BufferedReader reader = wrapper.getReader();
		String result = reader.readLine();
		String expected = HtmlUtils.htmlEscape("<script>alert('XSS')</script>"); // HTML로 인코딩된 예상 결과
		assertEquals(expected, result); // 결과가 예상 값과 일치하는지 확인
	}

	/**
	 * 빈 요청 본문을 처리하는 경우를 테스트합니다.
	 */
	@Test
	@DisplayName("Test Get Reader - Empty String")
	void testGetReaderEmpty() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(""))); // 빈 요청 본문

		XssFilter.XssRequestWrapper wrapper = new XssFilter.XssRequestWrapper(request);

		BufferedReader reader = wrapper.getReader();
		String result = reader.readLine();
		assertEquals("", result); // 결과가 빈 문자열인지 확인
	}
}