package com.shinhancard.toss.util;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

/**
 * 쿠키 관련 유틸리티 클래스입니다.
 * <p>
 * 쿠키를 생성, 업데이트, 삭제 및 조회하는 메서드를 제공합니다. 기본적인 보안 설정이 적용되어 있습니다.
 * </p>
 */
@UtilityClass
public class CookieUtil {

	private static final int DEFAULT_MAX_AGE = 60 * 60 * 24 * 7; // 기본 쿠키 만료 시간: 1주일

	/**
	 * JWT 토큰을 담은 쿠키를 생성합니다.
	 *
	 * @param name  쿠키의 이름
	 * @param value 쿠키의 값
	 * @return 설정된 쿠키 객체
	 */
	public static Cookie createCookie(String name, String value) {
		return createCookie(name, value, DEFAULT_MAX_AGE); // 기본 만료 시간으로 쿠키 생성
	}

	/**
	 * JWT 토큰을 담은 쿠키를 생성합니다.
	 *
	 * @param name   쿠키의 이름
	 * @param value  쿠키의 값
	 * @param maxAge 쿠키의 만료 시간 (초 단위)
	 * @return 설정된 쿠키 객체
	 */
	public static Cookie createCookie(String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value); // 쿠키 객체 생성
		configureCookie(cookie, maxAge); // 쿠키 설정
		return cookie;
	}

	/**
	 * 쿠키를 삭제합니다.
	 *
	 * @param response HTTP 응답 객체
	 * @param name     삭제할 쿠키의 이름
	 */
	public static void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, null); // 쿠키 값 null로 설정
		configureCookie(cookie, 0); // 만료 시간을 0으로 설정하여 즉시 삭제
		response.addCookie(cookie); // 응답에 쿠키 추가
	}

	/**
	 * 쿠키의 값을 업데이트합니다.
	 *
	 * @param response HTTP 응답 객체
	 * @param name     쿠키의 이름
	 * @param newValue 새로운 쿠키 값
	 */
	public static void updateCookie(HttpServletResponse response, String name, String newValue) {
		Cookie cookie = createCookie(name, newValue); // 새로운 값으로 쿠키 생성
		response.addCookie(cookie); // 응답에 쿠키 추가
	}

	/**
	 * 요청에서 특정 쿠키를 찾습니다.
	 *
	 * @param cookies 요청에서 받은 쿠키 배열
	 * @param name    찾을 쿠키의 이름
	 * @return 찾은 쿠키를 감싼 Optional 객체
	 */
	public static Optional<Cookie> getCookie(Cookie[] cookies, String name) {
		if (cookies == null) {
			return Optional.empty(); // 쿠키 배열이 null인 경우 빈 Optional 반환
		}

		for (Cookie cookie : cookies) {
			if (name.equals(cookie.getName())) {
				return Optional.of(cookie); // 쿠키를 찾은 경우 반환
			}
		}
		return Optional.empty(); // 쿠키를 찾지 못한 경우 빈 Optional 반환
	}

	/**
	 * 쿠키에 기본적인 보안 속성을 적용합니다.
	 *
	 * @param cookie 설정할 쿠키
	 * @param maxAge 쿠키의 만료 시간 (초 단위)
	 */
	private static void configureCookie(Cookie cookie, int maxAge) {
		cookie.setHttpOnly(true); // JavaScript에서 접근할 수 없게 설정
		cookie.setPath("/"); // 모든 경로에서 유효하도록 설정
		cookie.setMaxAge(maxAge); // 만료 시간 설정
		cookie.setSecure(true); // HTTPS를 통해서만 쿠키가 전송되도록 설정
	}
}