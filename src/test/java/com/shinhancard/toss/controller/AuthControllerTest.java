package com.shinhancard.toss.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.io.JwtRequest;
import com.shinhancard.toss.io.JwtResponse;
import com.shinhancard.toss.service.AuthService;

/**
 * {@link AuthController}의 테스트 클래스입니다.
 * <p>
 * 이 테스트 클래스는 인증 관련 API 엔드포인트에 대한 다양한 테스트 케이스를 포함하여,
 * 인증 성공, 실패 및 다양한 오류 상황을 검증합니다.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc; // MockMvc 인스턴스 주입

	@MockBean
	private AuthService authService; // AuthService 모킹

	/**
	 * 유효한 리다이렉트 URL로 리다이렉트되는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Authenticate user and redirect with valid URL")
	void testAuthenticateUserAndRedirectSuccess() throws Exception {
		// 인증 요청과 응답 설정
		JwtRequest jwtRequest = new JwtRequest("", "", "https://www.naver.com/", "", "");
		JwtResponse jwtResponse = new JwtResponse("", "validAccessToken", 0, "", 0, "");

		// AuthService 모킹
		Mockito.when(authService.authenticateWithTokens(jwtRequest)).thenReturn(jwtResponse);

		// API 호출 및 검증
		mockMvc.perform(post("/api/login-and-redirect")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jwtRequest.toString()))
			.andExpect(status().is3xxRedirection()) // 3xx 리다이렉션 상태 코드 검증
			.andExpect(MockMvcResultMatchers.redirectedUrl("https://www.naver.com/")); // 리다이렉트 URL 검증
	}

	/**
	 * JWT 토큰을 쿠키에 담아 반환하는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Authenticate user and return JWT token in cookie")
	void testAuthenticateUserWithCookieSuccess() throws Exception {
		// 인증 요청과 응답 설정
		JwtRequest jwtRequest = new JwtRequest("encryptedCi", "", "https://www.naver.com/", "", "");
		JwtResponse jwtResponse = new JwtResponse("", "validAccessToken", 0, "", 0, "");

		// AuthService 모킹
		Mockito.when(authService.authenticateWithTokens(jwtRequest)).thenReturn(jwtResponse);

		// API 호출 및 검증
		mockMvc.perform(post("/api/login-with-cookie")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"ci\":\"encryptedCi\"}"))
			.andExpect(status().isOk()) // 200 OK 상태 코드 검증
			.andExpect(MockMvcResultMatchers.content().string("Authentication successful")) // 응답 내용 검증
			.andExpect(MockMvcResultMatchers.cookie().exists("JWT")) // 쿠키 존재 여부 검증
			.andExpect(MockMvcResultMatchers.cookie().value("JWT", "validAccessToken")); // 쿠키 값 검증
	}

	/**
	 * JWT 토큰을 응답 헤더에 담아 반환하는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Authenticate user and return JWT token in header")
	void testAuthenticateUserWithHeaderSuccess() throws Exception {
		// 인증 요청과 응답 설정
		JwtRequest jwtRequest = new JwtRequest("encryptedCi", "", "https://www.naver.com/", "", "");
		JwtResponse jwtResponse = new JwtResponse("", "validAccessToken", 0, "", 0, "");

		// AuthService 모킹
		Mockito.when(authService.authenticateWithTokens(jwtRequest)).thenReturn(jwtResponse);

		// API 호출 및 검증
		mockMvc.perform(post("/api/login-with-header")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"ci\":\"encryptedCi\"}"))
			.andExpect(status().isOk()) // 200 OK 상태 코드 검증
			.andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer validAccessToken")) // 헤더 값 검증
			.andExpect(MockMvcResultMatchers.content().string("Authentication successful")); // 응답 내용 검증
	}

	/**
	 * JWT 토큰을 응답 바디에 담아 반환하는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Authenticate user and return JWT token in body")
	void testAuthenticateUserWithBodySuccess() throws Exception {
		// 인증 요청과 응답 설정
		JwtRequest jwtRequest = new JwtRequest("encryptedCi", "", "https://www.naver.com/", "", "");
		JwtResponse jwtResponse = new JwtResponse("", "validAccessToken", 0, "", 0, "");

		// AuthService 모킹
		Mockito.when(authService.authenticateWithTokens(jwtRequest)).thenReturn(jwtResponse);

		// API 호출 및 검증
		mockMvc.perform(post("/api/login-with-body")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"ci\":\"encryptedCi\"}"))
			.andExpect(status().isOk()) // 200 OK 상태 코드 검증
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value("validAccessToken")) // 응답 바디 검증
			.andExpect(MockMvcResultMatchers.content()
				.string("{\"success\":true,\"data\":{\"accessToken\":\"validAccessToken\"}}")); // 응답 내용 검증
	}

	/**
	 * 리다이렉트 URL이 없거나 비어 있을 때 오류가 발생하는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Authenticate user and handle invalid redirect URL")
	void testAuthenticateUserAndRedirectInvalidUrl() throws Exception {
		// 인증 요청과 응답 설정
		JwtRequest jwtRequest = new JwtRequest("encryptedCi", "", "https://www.naver.com/", "", "");
		JwtResponse jwtResponse = new JwtResponse("", "validAccessToken", 0, "", 0, "");

		// AuthService 모킹
		Mockito.when(authService.authenticateWithTokens(jwtRequest)).thenReturn(jwtResponse);

		// API 호출 및 검증
		mockMvc.perform(post("/api/login-and-redirect")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"ci\":\"encryptedCi\", \"redirectUrl\":\"\"}"))
			.andExpect(status().isBadRequest()) // 400 Bad Request 상태 코드 검증
			.andExpect(MockMvcResultMatchers.content().string("Invalid redirect URL")); // 오류 메시지 검증
	}

	/**
	 * 인증 실패 시 적절한 오류 메시지와 상태 코드가 반환되는지 테스트합니다.
	 *
	 * @throws Exception 요청 처리 중 예외 발생 시
	 */
	@Test
	@DisplayName("Handle authentication failure")
	void testAuthenticateUserFailure() throws Exception {
		// 인증 요청 설정
		JwtRequest jwtRequest = new JwtRequest("encryptedCi", "", "https://www.naver.com/", "", "");

		// AuthService 모킹: 인증 실패 시뮬레이션
		Mockito.when(authService.authenticateWithTokens(jwtRequest))
			.thenThrow(new AuthenticationException(ErrorCode.INVALID_CREDENTIALS));

		// API 호출 및 검증
		mockMvc.perform(post("/api/login-with-cookie")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"ci\":\"encryptedCi\"}"))
			.andExpect(status().isUnauthorized()) // 401 Unauthorized 상태 코드 검증
			.andExpect(MockMvcResultMatchers.content().string("Authentication failed")); // 오류 메시지 검증
	}
}