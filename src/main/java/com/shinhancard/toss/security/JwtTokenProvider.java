package com.shinhancard.toss.security;

import java.util.Collections;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.exception.InvalidTokenException;
import com.shinhancard.toss.exception.TokenCreationException;
import com.shinhancard.toss.exception.TokenRefreshException;
import com.shinhancard.toss.properties.JwtTokenProperties;
import com.shinhancard.toss.service.RedisService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * JWT 토큰을 생성, 검증 및 관리하는 클래스입니다.
 * <p>
 * Redis를 사용하여 토큰을 관리하며, 토큰 유효성을 검증하는 로직을 포함합니다.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements JwtTokenService {

	private final RedisService redisService; // Redis 서비스
	private final JwtTokenProperties jwtTokenProperties; // JWT 토큰 설정 값
	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(jwtTokenProperties.getSecret().getBytes());
	}

	/**
	 * 사용자명을 기반으로 JWT 토큰을 생성합니다.
	 *
	 * @param username 사용자명
	 * @return 생성된 JWT 토큰
	 */
	@Override
	public String createToken(String username) {
		try {
			Claims claims = (Claims)Jwts.claims().subject(username); // 사용자명을 클레임으로 설정
			Date now = new Date(); // 현재 시간
			Date validity = new Date(now.getTime() + jwtTokenProperties.getValidity()); // 유효기간 설정

			String accessToken = Jwts.builder()
				.claims(claims)
				.issuedAt(now)
				.expiration(validity)
				.signWith(secretKey) // 서명 설정
				.compact();

			// Redis에 엑세스 토큰 저장
			redisService.addToSet(getRedisKey(username), accessToken, jwtTokenProperties.getValidity() / 1000);

			return accessToken;
		} catch (Exception e) {
			throw new TokenCreationException(ErrorCode.TOKEN_CREATION_FAILED); // 토큰 생성 실패 시 예외 발생
		}
	}

	/**
	 * 리프레시 토큰을 생성합니다.
	 *
	 * @param username 사용자명
	 * @return 생성된 리프레시 토큰
	 */
	public String createRefreshToken(String username) {
		try {
			Claims claims = (Claims)Jwts.claims().subject(username);
			Date now = new Date();
			Date validity = new Date(now.getTime() + jwtTokenProperties.getRefreshValidity());

			String refreshToken = Jwts.builder()
				.claims(claims)
				.issuedAt(now)
				.expiration(validity)
				.signWith(secretKey)
				.compact();

			// Redis에 리프레시 토큰 저장
			redisService.addToSet(getRedisKey(username), refreshToken, jwtTokenProperties.getRefreshValidity() / 1000);

			return refreshToken;
		} catch (Exception e) {
			throw new TokenCreationException(ErrorCode.TOKEN_CREATION_FAILED);
		}
	}

	/**
	 * 기존 엑세스 토큰을 새 리프레시 토큰으로 갱신합니다.
	 *
	 * @param accessToken 기존 엑세스 토큰
	 * @return 새로 생성된 리프레시 토큰
	 */
	public String refreshToken(String accessToken) {
		try {
			String username = getUsername(accessToken);

			if (validateToken(accessToken)) {
				// 기존 엑세스 토큰 삭제
				redisService.removeFromSet(getRedisKey(username), accessToken);

				// 새 리프레시 토큰 생성 및 저장
				String refreshToken = createRefreshToken(username);
				redisService.addToSet(getRedisKey(username), refreshToken,
					jwtTokenProperties.getRefreshValidity() / 1000);

				return refreshToken;
			} else {
				throw new TokenRefreshException(ErrorCode.TOKEN_REFRESH_FAILED); // 갱신 실패 시 예외 발생
			}
		} catch (Exception e) {
			throw new TokenRefreshException(ErrorCode.TOKEN_REFRESH_FAILED);
		}
	}

	/**
	 * 토큰의 유효성을 검증합니다.
	 *
	 * @param token JWT 토큰
	 * @return 토큰이 유효한지 여부
	 */
	@Override
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);

			String username = getUsername(token);

			// Redis에서 토큰 존재 여부 확인
			return redisService.isMemberOfSet(getRedisKey(username), token);
		} catch (Exception e) {
			throw new InvalidTokenException(ErrorCode.TOKEN_INVALID);
		}
	}

	/**
	 * 토큰에서 사용자명을 추출합니다.
	 *
	 * @param token JWT 토큰
	 * @return 사용자명
	 */
	@Override
	public String getUsername(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
		} catch (Exception e) {
			throw new InvalidTokenException(ErrorCode.TOKEN_INVALID);
		}
	}

	/**
	 * JWT 토큰을 기반으로 인증 정보를 생성합니다.
	 *
	 * @param token JWT 토큰
	 * @return 인증 정보
	 */
	@Override
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = new User(getUsername(token), "", Collections.emptyList());
		return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
	}

	/**
	 * Redis 키를 생성합니다.
	 *
	 * @param username 사용자명
	 * @return Redis 키
	 */
	private String getRedisKey(String username) {
		return username + ":tokens";
	}
}