package com.shinhancard.toss.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CookieUtil의 기능을 테스트하기 위한 단위 테스트 클래스입니다.
 */
class CookieUtilTest {

	private HttpServletResponse response; // HTTP 응답 객체 Mock

	/**
	 * 테스트 실행 전에 Mock 객체를 초기화합니다.
	 */
	@BeforeEach
	void setUp() {
		response = mock(HttpServletResponse.class); // HTTP 응답 객체 Mock 초기화
	}

	/**
	 * 쿠키를 생성하는 메서드의 테스트입니다.
	 */
	@Test
	@DisplayName("쿠키 생성 테스트")
	void testCreateCookie() {
		String name = "testCookie";
		String value = "testValue";
		Cookie cookie = CookieUtil.createCookie(name, value);

		assertNotNull(cookie); // 쿠키가 null이 아님을 검증
		assertEquals(name, cookie.getName()); // 쿠키 이름 검증
		assertEquals(value, cookie.getValue()); // 쿠키 값 검증
		assertEquals(60 * 60 * 24 * 7, cookie.getMaxAge()); // 기본 만료 시간 검증
		assertTrue(cookie.isHttpOnly()); // HTTP 전용 속성 검증
		assertTrue(cookie.getSecure()); // 보안 속성 검증
		assertEquals("/", cookie.getPath()); // 경로 설정 검증
	}

	/**
	 * 만료 시간을 설정하여 쿠키를 생성하는 메서드의 테스트입니다.
	 */
	@Test
	@DisplayName("만료 시간 설정 쿠키 생성 테스트")
	void testCreateCookieWithMaxAge() {
		String name = "testCookie";
		String value = "testValue";
		int maxAge = 3600; // 1시간

		Cookie cookie = CookieUtil.createCookie(name, value, maxAge);

		assertNotNull(cookie); // 쿠키가 null이 아님을 검증
		assertEquals(name, cookie.getName()); // 쿠키 이름 검증
		assertEquals(value, cookie.getValue()); // 쿠키 값 검증
		assertEquals(maxAge, cookie.getMaxAge()); // 만료 시간 검증
	}

	/**
	 * 쿠키를 삭제하는 메서드의 테스트입니다.
	 */
	@Test
	@DisplayName("쿠키 삭제 테스트")
	void testDeleteCookie() {
		String name = "testCookie";

		CookieUtil.deleteCookie(response, name);

		Cookie cookie = new Cookie(name, null); // 삭제할 쿠키 생성
		verify(response).addCookie(argThat(c ->
			name.equals(c.getName()) && c.getMaxAge() == 0)); // 쿠키 삭제 호출 검증
	}

	/**
	 * 쿠키의 값을 업데이트하는 메서드의 테스트입니다.
	 */
	@Test
	@DisplayName("쿠키 업데이트 테스트")
	void testUpdateCookie() {
		String name = "testCookie";
		String newValue = "newValue";

		CookieUtil.updateCookie(response, name, newValue);

		Cookie cookie = CookieUtil.createCookie(name, newValue); // 업데이트된 쿠키 생성
		verify(response).addCookie(argThat(c ->
			name.equals(c.getName()) && newValue.equals(c.getValue()))); // 쿠키 업데이트 호출 검증
	}

	/**
	 * 쿠키 배열에서 특정 쿠키를 찾는 메서드의 테스트입니다.
	 */
	@Test
	@DisplayName("쿠키 찾기 테스트")
	void testGetCookie() {
		Cookie[] cookies = {
			new Cookie("cookie1", "value1"),
			new Cookie("cookie2", "value2")
		};

		Optional<Cookie> cookie = CookieUtil.getCookie(cookies, "cookie2");

		assertTrue(cookie.isPresent()); // 쿠키가 존재함을 검증
		assertEquals("cookie2", cookie.get().getName()); // 쿠키 이름 검증
		assertEquals("value2", cookie.get().getValue()); // 쿠키 값 검증
	}

	/**
	 * 쿠키 배열에서 특정 쿠키가 없는 경우의 테스트입니다.
	 */
	@Test
	@DisplayName("쿠키가 없는 경우 찾기 테스트")
	void testGetCookieNotFound() {
		Cookie[] cookies = {
			new Cookie("cookie1", "value1")
		};

		Optional<Cookie> cookie = CookieUtil.getCookie(cookies, "cookie2");

		assertFalse(cookie.isPresent()); // 쿠키가 존재하지 않음을 검증
	}

	/**
	 * 쿠키 배열이 null인 경우 쿠키를 찾는 메서드의 테스트입니다.
	 */
	@Test
	@DisplayName("쿠키 배열이 null인 경우 찾기 테스트")
	void testGetCookieWithNullArray() {
		Cookie[] cookies = null;

		Optional<Cookie> cookie = CookieUtil.getCookie(cookies, "cookie1");

		assertFalse(cookie.isPresent()); // 쿠키가 존재하지 않음을 검증
	}

	/**
	 * 쿠키의 기본 보안 속성이 적용되었는지 검증합니다.
	 */
	@Test
	@DisplayName("쿠키 보안 속성 검증")
	void testCookieSecurityAttributes() {
		String name = "secureCookie";
		String value = "secureValue";
		Cookie cookie = CookieUtil.createCookie(name, value);

		assertTrue(cookie.isHttpOnly()); // HTTP 전용 속성 검증
		assertTrue(cookie.getSecure()); // 보안 속성 검증
		assertEquals("/", cookie.getPath()); // 경로 설정 검증
	}
}
