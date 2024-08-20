package com.shinhancard.toss.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.shinhancard.toss.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(RedirectController.class)
public class RedirectControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthService authService;

	/**
	 * 유효한 토큰이 있을 때 리다이렉션이 성공하는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Successful redirection with valid token")
	public void testRedirectWithValidToken() throws Exception {
		// 토큰 검증이 성공하도록 AuthService 모킹
		Mockito.when(authService.getTokenFromCookie(Mockito.any())).thenReturn("validToken");
		Mockito.when(authService.validateToken("validToken")).thenReturn(true);

		// API 호출 및 검증
		mockMvc.perform(get("/api/redirect")
				.cookie(new Cookie("JWT", "validToken")))
			.andExpect(status().is3xxRedirection()) // 3xx 리다이렉션 상태 코드 검증
			.andExpect(MockMvcResultMatchers.redirectedUrl("https://other-domain.com/success")); // 리다이렉트 URL 검증
	}

	/**
	 * 유효하지 않은 토큰으로 요청했을 때 인증 오류가 발생하는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Unauthorized redirection with invalid token")
	public void testRedirectWithInvalidToken() throws Exception {
		// 토큰 검증이 실패하도록 AuthService 모킹
		Mockito.when(authService.getTokenFromCookie(Mockito.any())).thenReturn("invalidToken");
		Mockito.when(authService.validateToken("invalidToken")).thenReturn(false);

		// API 호출 및 검증
		mockMvc.perform(get("/api/redirect")
				.cookie(new Cookie("JWT", "invalidToken")))
			.andExpect(status().isUnauthorized()) // 401 Unauthorized 상태 코드 검증
			.andExpect(MockMvcResultMatchers.content().string("Token is invalid")); // 오류 메시지 검증
	}

	/**
	 * 쿠키가 없는 경우 리다이렉션 요청 시 인증 오류가 발생하는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Unauthorized redirection when no cookie is present")
	public void testRedirectWithoutCookie() throws Exception {
		// 토큰이 없는 경우로 AuthService 모킹
		Mockito.when(authService.getTokenFromCookie(Mockito.any())).thenReturn(null);

		// API 호출 및 검증
		mockMvc.perform(get("/api/redirect"))
			.andExpect(status().isUnauthorized()) // 401 Unauthorized 상태 코드 검증
			.andExpect(MockMvcResultMatchers.content().string("Token is invalid")); // 오류 메시지 검증
	}

	/**
	 * 리다이렉션 중 IOException이 발생할 때 적절히 처리되는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Handle IOException during redirection")
	public void testRedirectionIOException() throws Exception {
		// 토큰 검증이 성공하도록 AuthService 모킹
		Mockito.when(authService.getTokenFromCookie(Mockito.any())).thenReturn("validToken");
		Mockito.when(authService.validateToken("validToken")).thenReturn(true);

		// IOException 발생시키기 위해 MockHttpServletResponse 사용
		MockHttpServletResponse response = new MockHttpServletResponse() {
			@Override
			public void sendRedirect(String location) throws IOException {
				throw new IOException("Forced IOException");
			}
		};

		// API 호출 및 검증
		mockMvc.perform(get("/api/redirect")
				.cookie(new Cookie("JWT", "validToken")))
			.andExpect(status().isInternalServerError()) // 500 Internal Server Error 상태 코드 검증
			.andExpect(MockMvcResultMatchers.content().string("Redirection failed: Forced IOException")); // 오류 메시지 검증
	}

	/**
	 * 유효한 토큰으로 리다이렉션 후 쿠키에 잘못된 정보가 포함된 경우를 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Redirect with valid token but invalid cookie content")
	public void testRedirectWithValidTokenInvalidCookieContent() throws Exception {
		// 토큰 검증이 성공하도록 AuthService 모킹
		Mockito.when(authService.getTokenFromCookie(Mockito.any())).thenReturn("validToken");
		Mockito.when(authService.validateToken("validToken")).thenReturn(true);

		// 쿠키에 잘못된 정보가 포함된 경우로 Mocking
		Mockito.when(authService.getTokenFromCookie(Mockito.any(HttpServletRequest.class)))
			.thenReturn("validToken");

		// API 호출 및 검증
		mockMvc.perform(get("/api/redirect")
				.cookie(new Cookie("JWT", "invalidTokenContent")))
			.andExpect(status().isUnauthorized()) // 401 Unauthorized 상태 코드 검증
			.andExpect(MockMvcResultMatchers.content().string("Token is invalid")); // 오류 메시지 검증
	}

	/**
	 * 유효한 토큰을 가진 경우에만 리다이렉션 URL이 정확히 설정되어야 함을 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Redirect URL is correctly set with valid token")
	public void testRedirectUrlWithValidToken() throws Exception {
		// 토큰 검증이 성공하도록 AuthService 모킹
		Mockito.when(authService.getTokenFromCookie(Mockito.any())).thenReturn("validToken");
		Mockito.when(authService.validateToken("validToken")).thenReturn(true);

		// API 호출 및 검증
		mockMvc.perform(get("/api/redirect")
				.cookie(new Cookie("JWT", "validToken")))
			.andExpect(status().is3xxRedirection()) // 3xx 리다이렉션 상태 코드 검증
			.andExpect(MockMvcResultMatchers.redirectedUrl("https://other-domain.com/success")); // 리다이렉트 URL 검증
	}
}