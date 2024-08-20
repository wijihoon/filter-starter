package com.shinhancard.toss.service;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.io.JwtRequest;
import com.shinhancard.toss.io.JwtResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 * 이 인터페이스는 사용자 인증, 토큰 생성, 토큰 검증 및 쿠키에서 토큰 추출 기능을 제공합니다.
 */
public interface AuthService {

	/**
	 * 암호화된 CI를 사용하여 사용자 인증을 수행합니다.
	 *
	 * @param encryptedCi 암호화된 사용자 CI
	 * @return 인증 결과로 생성된 JWT 토큰
	 */
	String authenticate(String encryptedCi);

	/**
	 * 사용자 이름을 기반으로 JWT 토큰을 생성합니다.
	 *
	 * @param username 사용자 이름
	 * @return 생성된 JWT 토큰
	 */
	String createToken(String username);

	/**
	 * HTTP 요청의 쿠키에서 JWT 토큰을 추출합니다.
	 *
	 * @param request HTTP 요청 객체
	 * @return 쿠키에서 추출한 JWT 토큰
	 */
	String getTokenFromCookie(HttpServletRequest request);

	/**
	 * 주어진 JWT 토큰의 유효성을 검증합니다.
	 *
	 * @param token JWT 토큰
	 * @return 토큰이 유효하면 true, 그렇지 않으면 false
	 */
	boolean validateToken(String token);

	/**
	 * 사용자의 인증을 시도하고, JWT 토큰과 리프레시 토큰을 생성하여 반환합니다.
	 *
	 * @param jwtRequest JWT 요청 데이터 전송 객체
	 * @return 인증 결과를 담은 JwtResponse 객체
	 * @throws AuthenticationException 인증 실패 시 발생할 수 있는 예외
	 */
	JwtResponse authenticateWithTokens(JwtRequest jwtRequest) throws AuthenticationException;
}