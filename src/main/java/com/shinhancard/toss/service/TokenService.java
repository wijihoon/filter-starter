package com.shinhancard.toss.service;

import com.shinhancard.toss.exception.InvalidTokenException;
import com.shinhancard.toss.exception.TokenCreationException;
import com.shinhancard.toss.io.TokenResponse;

/**
 * JWT 토큰을 생성, 검증 및 관리하는 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 JWT 토큰 생성, 유효성 검증 및 토큰에서 사용자명 추출 기능을 정의합니다.
 * </p>
 */
public interface TokenService {

	/**
	 * 사용자명 기반으로 JWT 토큰을 생성합니다.
	 *
	 * @param jwtResponse 사용자명 및 관련 정보를 포함한 TokenResponse 객체
	 * @return 생성된 JWT 토큰을 포함하는 TokenResponse 객체
	 * @throws TokenCreationException 토큰 생성 실패 시 발생하는 예외
	 */
	TokenResponse createToken(TokenResponse jwtResponse) throws TokenCreationException;

	/**
	 * JWT 토큰에서 사용자명을 추출합니다.
	 *
	 * @param token JWT 토큰
	 * @return 사용자명 (subject)
	 * @throws InvalidTokenException 토큰이 유효하지 않을 때 발생하는 예외
	 */
	String getSubject(String token) throws InvalidTokenException;
}
