package com.shinhancard.toss.security;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.exception.JwtTokenException;

/**
 * JwtAuthenticationFilter의 테스트 클래스입니다.
 * <p>
 * 이 클래스는 JWT 인증 필터의 다양한 시나리오를 테스트하여 필터의 동작을 검증합니다.
 * </p>
 */
@WebMvcTest // Web MVC 계층의 테스트를 수행하기 위한 어노테이션입니다.
public class JwtAuthenticationFilterTest {

	@Autowired
	private MockMvc mockMvc; // MockMvc 객체를 주입받습니다.

	@MockBean
	private JwtTokenService jwtTokenService; // JwtTokenService를 모의 객체로 주입받습니다.

	@BeforeEach
	public void setUp() {
		// SecurityContext를 모의 객체로 설정합니다.
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	@DisplayName("유효한 JWT 토큰으로 인증 성공")
	public void testSuccessfulAuthentication() throws Exception {
		String token = "valid-jwt-token"; // 유효한 토큰 문자열
		Authentication authentication = Mockito.mock(Authentication.class); // 모의 Authentication 객체
		// JWT 토큰이 유효하다고 설정합니다.
		when(jwtTokenService.validateToken(eq(token))).thenReturn(true);
		// JWT 토큰으로부터 인증 정보를 생성한다고 설정합니다.
		when(jwtTokenService.getAuthentication(eq(token))).thenReturn(authentication);

		// MockMvc를 사용하여 요청을 보내고 응답 상태 코드가 200 OK인지 검증합니다.
		mockMvc.perform(MockMvcRequestBuilders.get("/some-secure-endpoint")
				.header("Authorization", "Bearer " + token))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("유효하지 않은 JWT 토큰으로 인증 실패")
	public void testUnsuccessfulAuthenticationDueToInvalidToken() throws Exception {
		String token = "invalid-jwt-token"; // 유효하지 않은 토큰 문자열
		// JWT 토큰이 유효하지 않다고 설정합니다.
		when(jwtTokenService.validateToken(eq(token))).thenReturn(false);

		// MockMvc를 사용하여 요청을 보내고 응답 상태 코드가 401 Unauthorized인지 검증합니다.
		mockMvc.perform(MockMvcRequestBuilders.get("/some-secure-endpoint")
				.header("Authorization", "Bearer " + token))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(result -> {
				// 응답에서 발생한 예외가 JwtTokenException인지 검증하고, 에러 코드가 JWT_TOKEN_INVALID인지 확인합니다.
				JwtTokenException exception = (JwtTokenException)result.getResolvedException();
				assert Objects.equals(Objects.requireNonNull(exception).getErrorCode(),
					ErrorCode.JWT_TOKEN_INVALID.getCode());
			});
	}

	@Test
	@DisplayName("JWT 토큰이 없는 경우 인증 실패")
	public void testUnsuccessfulAuthenticationDueToMissingToken() throws Exception {
		// 토큰이 없는 요청을 보내고 응답 상태 코드가 401 Unauthorized인지 검증합니다.
		mockMvc.perform(MockMvcRequestBuilders.get("/some-secure-endpoint"))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(result -> {
				// 응답에서 발생한 예외가 JwtTokenException인지 검증하고, 에러 코드가 JWT_TOKEN_MISSING인지 확인합니다.
				JwtTokenException exception = (JwtTokenException)result.getResolvedException();
				assert Objects.equals(Objects.requireNonNull(exception).getErrorCode(),
					ErrorCode.JWT_TOKEN_MISSING.getCode());
			});
	}

	@Test
	@DisplayName("형식이 잘못된 JWT 토큰으로 인증 실패")
	public void testUnsuccessfulAuthenticationWithMalformedToken() throws Exception {
		String token = "malformed-token"; // 형식이 잘못된 토큰 문자열
		// JWT 토큰이 형식이 잘못되었다고 설정합니다.
		when(jwtTokenService.validateToken(eq(token))).thenThrow(new JwtTokenException(ErrorCode.JWT_TOKEN_MALFORMED));

		// MockMvc를 사용하여 요청을 보내고 응답 상태 코드가 401 Unauthorized인지 검증합니다.
		mockMvc.perform(MockMvcRequestBuilders.get("/some-secure-endpoint")
				.header("Authorization", "Bearer " + token))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(result -> {
				// 응답에서 발생한 예외가 JwtTokenException인지 검증하고, 에러 코드가 JWT_TOKEN_MALFORMED인지 확인합니다.
				JwtTokenException exception = (JwtTokenException)result.getResolvedException();
				assert Objects.equals(Objects.requireNonNull(exception).getErrorCode(),
					ErrorCode.JWT_TOKEN_MALFORMED.getCode());
			});
	}

	@Test
	@DisplayName("다른 유효한 JWT 토큰 형식으로 인증 성공")
	public void testSuccessfulAuthenticationWithDifferentTokenFormat() throws Exception {
		String token = "another-valid-jwt-token"; // 또 다른 유효한 토큰 문자열
		Authentication authentication = Mockito.mock(Authentication.class); // 모의 Authentication 객체
		// JWT 토큰이 유효하다고 설정합니다.
		when(jwtTokenService.validateToken(eq(token))).thenReturn(true);
		// JWT 토큰으로부터 인증 정보를 생성한다고 설정합니다.
		when(jwtTokenService.getAuthentication(eq(token))).thenReturn(authentication);

		// MockMvc를 사용하여 요청을 보내고 응답 상태 코드가 200 OK인지 검증합니다.
		mockMvc.perform(MockMvcRequestBuilders.get("/another-secure-endpoint")
				.header("Authorization", "Bearer " + token))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("접두사 없는 JWT 토큰으로 인증 실패")
	public void testSuccessfulAuthenticationWithValidTokenWithoutPrefix() throws Exception {
		String token = "valid-jwt-token"; // 유효한 토큰 문자열
		Authentication authentication = Mockito.mock(Authentication.class); // 모의 Authentication 객체
		// JWT 토큰이 유효하다고 설정합니다.
		when(jwtTokenService.validateToken(eq(token))).thenReturn(true);
		// JWT 토큰으로부터 인증 정보를 생성한다고 설정합니다.
		when(jwtTokenService.getAuthentication(eq(token))).thenReturn(authentication);

		// 'Bearer ' 접두사 없는 토큰으로 요청을 보내고 응답 상태 코드가 401 Unauthorized인지 검증합니다.
		mockMvc.perform(MockMvcRequestBuilders.get("/some-secure-endpoint")
				.header("Authorization", token))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("빈 JWT 토큰으로 인증 실패")
	public void testSuccessfulAuthenticationWithEmptyToken() throws Exception {
		String token = ""; // 빈 토큰 문자열
		// JWT 토큰이 없다고 설정합니다.
		when(jwtTokenService.validateToken(eq(token))).thenThrow(new JwtTokenException(ErrorCode.JWT_TOKEN_MISSING));

		// 빈 토큰으로 요청을 보내고 응답 상태 코드가 401 Unauthorized인지 검증합니다.
		mockMvc.perform(MockMvcRequestBuilders.get("/some-secure-endpoint")
				.header("Authorization", "Bearer " + token))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(result -> {
				// 응답에서 발생한 예외가 JwtTokenException인지 검증하고, 에러 코드가 JWT_TOKEN_MISSING인지 확인합니다.
				JwtTokenException exception = (JwtTokenException)result.getResolvedException();
				assert Objects.equals(Objects.requireNonNull(exception).getErrorCode(),
					ErrorCode.JWT_TOKEN_MISSING.getCode());
			});
	}
}