package com.shinhancard.toss.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.io.JwtRequest;
import com.shinhancard.toss.io.JwtResponse;
import com.shinhancard.toss.security.JwtTokenService;
import com.shinhancard.toss.service.impl.AuthServiceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * AuthServiceImpl 클래스의 기능을 테스트하기 위한 단위 테스트 클래스입니다.
 */
class AuthServiceTest {

	// JwtTokenService를 Mock 객체로 설정
	@Mock
	private JwtTokenService jwtTokenService;

	// RedisService를 Mock 객체로 설정
	@Mock
	private RedisService redisService;

	// AuthServiceImpl 객체를 테스트 대상으로 설정
	@InjectMocks
	private AuthServiceImpl authService;

	/**
	 * 테스트 실행 전에 Mock 객체를 초기화합니다.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Mockito의 Mock 객체를 초기화
	}

	/**
	 * 유효한 CI로 인증 성공 테스트
	 */
	@Test
	@DisplayName("유효한 CI로 인증 성공 테스트")
	void testAuthenticateSuccess() {
		String encryptedCi = Base64.getEncoder().encodeToString("validCi".getBytes()); // "validCi"를 Base64로 인코딩
		when(authService.decryptCi(encryptedCi)).thenReturn("validCi"); // decryptCi 메서드의 반환값 설정
		when(authService.isValidUser("validCi")).thenReturn(true); // isValidUser 메서드의 반환값 설정
		when(jwtTokenService.createToken("validCi")).thenReturn("accessToken"); // createToken 메서드의 반환값 설정

		String token = authService.authenticate(encryptedCi); // 인증 메서드 호출

		assertEquals("accessToken", token); // 반환된 토큰이 예상값과 일치하는지 검증
	}

	/**
	 * 유효하지 않은 CI로 인증 실패 테스트
	 */
	@Test
	@DisplayName("유효하지 않은 CI로 인증 실패 테스트")
	void testAuthenticateFailure() {
		String encryptedCi = Base64.getEncoder().encodeToString("invalidCi".getBytes()); // "invalidCi"를 Base64로 인코딩
		when(authService.decryptCi(encryptedCi)).thenReturn("invalidCi"); // decryptCi 메서드의 반환값 설정
		when(authService.isValidUser("invalidCi")).thenReturn(false); // isValidUser 메서드의 반환값 설정

		assertThrows(AuthenticationException.class, () -> authService.authenticate(encryptedCi)); // 인증 실패 시 예외 발생 검증
	}

	/**
	 * JWT 쿠키 추출 성공 테스트
	 */
	@Test
	@DisplayName("JWT 쿠키 추출 성공 테스트")
	void testGetTokenFromCookieSuccess() {
		HttpServletRequest request = mock(HttpServletRequest.class); // HttpServletRequest Mock 객체 생성
		Cookie jwtCookie = new Cookie("JWT", "accessToken"); // "JWT"라는 이름의 쿠키 생성
		Cookie[] cookies = {jwtCookie}; // 쿠키 배열 생성
		when(request.getCookies()).thenReturn(cookies); // getCookies 메서드의 반환값 설정

		String token = authService.getTokenFromCookie(request); // 쿠키에서 토큰 추출 메서드 호출

		assertEquals("accessToken", token); // 추출된 토큰이 예상값과 일치하는지 검증
	}

	/**
	 * JWT 쿠키 추출 실패 테스트
	 */
	@Test
	@DisplayName("JWT 쿠키 추출 실패 테스트")
	void testGetTokenFromCookieFailure() {
		HttpServletRequest request = mock(HttpServletRequest.class); // HttpServletRequest Mock 객체 생성
		when(request.getCookies()).thenReturn(null); // getCookies 메서드의 반환값을 null로 설정

		assertThrows(AuthenticationException.class, () -> authService.getTokenFromCookie(request)); // 쿠키가 없을 때 예외 발생 검증
	}

	/**
	 * 토큰 검증 성공 테스트
	 */
	@Test
	@DisplayName("토큰 검증 성공 테스트")
	void testValidateTokenSuccess() {
		when(jwtTokenService.validateToken("validToken")).thenReturn(true); // validateToken 메서드의 반환값 설정

		boolean isValid = authService.validateToken("validToken"); // 토큰 검증 메서드 호출

		assertTrue(isValid); // 검증된 토큰이 유효한지 검증
	}

	/**
	 * 토큰 검증 실패 테스트
	 */
	@Test
	@DisplayName("토큰 검증 실패 테스트")
	void testValidateTokenFailure() {
		when(jwtTokenService.validateToken("invalidToken")).thenReturn(false); // validateToken 메서드의 반환값 설정

		boolean isValid = authService.validateToken("invalidToken"); // 토큰 검증 메서드 호출

		assertFalse(isValid); // 검증된 토큰이 유효하지 않은지 검증
	}

	/**
	 * JWT 요청 데이터 전송 객체로 인증 및 토큰 생성 테스트
	 */
	@Test
	@DisplayName("JWT 요청 데이터 전송 객체로 인증 및 토큰 생성 테스트")
	void testAuthenticateWithTokens() {
		JwtRequest jwtRequest = new JwtRequest("validUsername", "", "", "", ""); // JwtRequest 객체 생성
		when(authService.isValidUser("validUsername")).thenReturn(true); // isValidUser 메서드의 반환값 설정
		when(jwtTokenService.createToken("validUsername")).thenReturn("accessToken"); // createToken 메서드의 반환값 설정
		when(jwtTokenService.createRefreshToken("validUsername")).thenReturn(
			"refreshToken"); // createRefreshToken 메서드의 반환값 설정

		JwtResponse response = authService.authenticateWithTokens(jwtRequest); // 인증 및 토큰 생성 메서드 호출

		assertEquals("accessToken", response.accessToken()); // 생성된 엑세스 토큰 검증
		assertEquals("refreshToken", response.refreshToken()); // 생성된 리프레시 토큰 검증
	}
}
