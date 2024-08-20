package com.shinhancard.toss.security;

import org.springframework.security.core.Authentication;

/**
 * JWT 토큰을 생성하고 검증하는 서비스 인터페이스입니다.
 * JWT 토큰의 생성, 검증, 사용자 이름 추출, 인증 객체 생성을 위한 메소드들을 정의합니다.
 */
public interface JwtTokenService {

	/**
	 * 주어진 사용자 이름을 기반으로 JWT 토큰을 생성합니다.
	 *
	 * @param username 사용자 이름
	 * @return 생성된 JWT 토큰
	 */
	String createToken(String username);

	/**
	 * 주어진 사용자 이름을 기반으로 JWT 리프레시 토큰을 생성합니다.
	 *
	 * @param username 사용자 이름
	 * @return 생성된 JWT 리프레시 토큰
	 */
	String createRefreshToken(String username);

	/**
	 * JWT 토큰의 유효성을 검증합니다.
	 *
	 * @param token JWT 토큰
	 * @return 토큰이 유효하면 true, 그렇지 않으면 false
	 */
	boolean validateToken(String token);

	/**
	 * JWT 토큰에서 사용자 이름을 추출합니다.
	 *
	 * @param token JWT 토큰
	 * @return JWT 토큰에서 추출된 사용자 이름
	 */
	String getUsername(String token);

	/**
	 * JWT 토큰에서 인증 객체를 생성합니다.
	 *
	 * @param token JWT 토큰
	 * @return 생성된 인증 객체
	 */
	Authentication getAuthentication(String token);
}