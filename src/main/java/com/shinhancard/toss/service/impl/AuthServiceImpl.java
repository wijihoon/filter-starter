package com.shinhancard.toss.service.impl;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.io.JwtRequest;
import com.shinhancard.toss.io.JwtResponse;
import com.shinhancard.toss.security.JwtTokenService;
import com.shinhancard.toss.service.AuthService;
import com.shinhancard.toss.service.RedisService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * 이 클래스는 사용자의 인증, 토큰 생성, 쿠키에서 토큰 추출 및 토큰 검증을 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	// JWT 토큰 생성을 처리하는 서비스
	private final JwtTokenService jwtTokenService;

	// Redis 관련 로직을 처리하는 서비스
	private final RedisService redisService;

	/**
	 * 암호화된 CI를 사용하여 인증을 수행하고 JWT 엑세스 토큰을 생성합니다.
	 *
	 * @param encryptedCi 암호화된 CI
	 * @return 생성된 JWT 엑세스 토큰
	 * @throws AuthenticationException 인증 실패 시 발생할 수 있는 예외
	 */
	@Override
	public String authenticate(String encryptedCi) {
		String ci = decryptCi(encryptedCi); // CI 복호화

		// 사용자 CI가 유효한지 검증
		if (isValidUser(ci)) {
			return createToken(ci); // 유효한 경우 토큰 생성
		} else {
			log.warn("Invalid credentials provided for CI: {}", ci); // 유효하지 않으면 경고 로그 출력
			throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS); // 예외 발생
		}
	}

	/**
	 * 암호화된 CI를 Base64로 복호화합니다.
	 *
	 * @param encryptedCi 암호화된 CI
	 * @return 복호화된 CI
	 * @throws AuthenticationException 복호화 실패 시 발생하는 예외
	 */
	public String decryptCi(String encryptedCi) {
		try {
			return new String(Base64.getDecoder().decode(encryptedCi)); // Base64 복호화 수행
		} catch (IllegalArgumentException e) {
			log.error("Failed to decode CI: {}", encryptedCi, e); // 복호화 실패 시 오류 로그 출력
			throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS); // 예외 발생
		}
	}

	/**
	 * 사용자 CI의 유효성을 검사합니다.
	 *
	 * @param ci 복호화된 CI
	 * @return 유효한 사용자이면 true, 그렇지 않으면 false
	 */
	public boolean isValidUser(String ci) {
		// TODO: 실제 사용자 검증 로직으로 교체 필요
		return !ci.isEmpty(); // 임시 구현: 빈 문자열이 아닌 경우 유효한 사용자로 간주
	}

	/**
	 * 사용자 이름으로 JWT 엑세스 토큰을 생성합니다.
	 *
	 * @param username 사용자 이름
	 * @return 생성된 JWT 엑세스 토큰
	 */
	@Override
	public String createToken(String username) {
		String token = jwtTokenService.createToken(username); // JWT 토큰 생성
		log.info("Created token for username: {}", username); // 토큰 생성 로그 출력
		return token;
	}

	/**
	 * HTTP 요청에서 JWT 쿠키를 추출합니다.
	 *
	 * @param request HTTP 요청
	 * @return JWT 쿠키의 값
	 * @throws AuthenticationException JWT 쿠키가 없는 경우 발생할 수 있는 예외
	 */
	@Override
	public String getTokenFromCookie(HttpServletRequest request) {
		return Optional.ofNullable(request.getCookies()) // 쿠키가 존재하는지 확인
			.flatMap(cookies -> Optional.ofNullable(findJwtCookie(cookies))) // JWT 쿠키 검색
			.map(Cookie::getValue) // 쿠키 값 추출
			.orElseThrow(() -> { // 쿠키가 없으면 예외 발생
				log.warn("JWT cookie not found.");
				return new AuthenticationException(ErrorCode.TOKEN_INVALID);
			});
	}

	/**
	 * 쿠키 배열에서 JWT 쿠키를 찾습니다.
	 *
	 * @param cookies 쿠키 배열
	 * @return JWT 쿠키 또는 null
	 */
	private Cookie findJwtCookie(Cookie[] cookies) {
		return Arrays.stream(cookies) // 쿠키 배열을 스트림으로 변환
			.filter(cookie -> "JWT".equals(cookie.getName())) // 이름이 "JWT"인 쿠키 검색
			.findFirst() // 첫 번째 결과 반환
			.orElse(null); // 없으면 null 반환
	}

	/**
	 * 주어진 JWT 토큰의 유효성을 검사합니다.
	 *
	 * @param token JWT 토큰
	 * @return 유효한 토큰이면 true, 그렇지 않으면 false
	 */
	@Override
	public boolean validateToken(String token) {
		try {
			return jwtTokenService.validateToken(token); // 토큰 유효성 검증
		} catch (Exception e) {
			log.warn("Token validation failed: {}", e.getMessage()); // 유효성 검증 실패 시 경고 로그 출력
			return false;
		}
	}

	/**
	 * JWT 요청 데이터 전송 객체를 사용하여 사용자 인증을 수행하고, JWT 엑세스 및 리프레시 토큰을 생성하여 반환합니다.
	 *
	 * @param jwtRequest JWT 요청 데이터 전송 객체
	 * @return 인증 결과를 담은 JwtResponse 객체
	 * @throws AuthenticationException 인증 실패 시 발생할 수 있는 예외
	 */
	@Override
	public JwtResponse authenticateWithTokens(JwtRequest jwtRequest) throws AuthenticationException {
		String username = jwtRequest.clientId(); // JwtRequest에서 사용자 이름 가져오기

		// 사용자 이름이 유효한지 검증
		if (!isValidUser(username)) {
			log.warn("Invalid credentials provided for username: {}", username);
			throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
		}

		// 엑세스 토큰 및 리프레시 토큰 생성
		String accessToken = jwtTokenService.createToken(username);
		String refreshToken = jwtTokenService.createRefreshToken(username);

		// JwtResponse 객체로 결과 반환
		return new JwtResponse("", accessToken, 0, refreshToken, 0, "");
	}
}
