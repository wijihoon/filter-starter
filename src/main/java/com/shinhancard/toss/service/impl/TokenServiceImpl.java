package com.shinhancard.toss.service.impl;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.exception.InvalidTokenException;
import com.shinhancard.toss.exception.TokenCreationException;
import com.shinhancard.toss.io.TokenResponse;
import com.shinhancard.toss.properties.TokenProperties;
import com.shinhancard.toss.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰을 생성, 검증 및 관리하는 클래스입니다.
 * <p>
 * 이 클래스는 JWT 토큰을 생성하고, 유효성을 검증하며, 토큰에서 사용자명을 추출하는 기능을 제공합니다.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

	private final TokenProperties tokenProperties; // JWT 토큰 설정 값
	private SecretKey secretKey;

	/**
	 * HMAC-SHA256 알고리즘에 맞는 SecretKey를 생성합니다.
	 * <p>
	 * 이 메소드는 클래스가 초기화될 때 호출되어 비밀 키를 생성합니다.
	 * </p>
	 */
	@PostConstruct
	public void init() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(SignatureAlgorithm.HS256.getJcaName());
			keyGenerator.init(256); // 비밀 키 길이 설정 (256비트)
			this.secretKey = keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("비밀 키 생성에 실패했습니다.", e);
		}
	}

	/**
	 * 사용자명 기반으로 JWT 토큰을 생성합니다.
	 *
	 * @param jwtResponse 사용자명 및 관련 정보를 포함한 TokenResponse 객체
	 * @return 생성된 JWT 토큰을 포함하는 TokenResponse 객체
	 */
	@Override
	public TokenResponse createToken(TokenResponse jwtResponse) {
		try {
			Date now = new Date();
			Date validity = new Date(now.getTime() + tokenProperties.getValidity());

			String accessToken = Jwts.builder()
				.setClaims(new ObjectMapper().convertValue(jwtResponse, new TypeReference<Map<String, Object>>() {
				})) // 클레임 설정
				.setIssuedAt(now) // 발행일 설정
				.setExpiration(validity) // 만료일 설정
				.signWith(SignatureAlgorithm.HS256, secretKey) // 서명 설정 (HS256 알고리즘 사용)
				.compact(); // 토큰 생성

			return new TokenResponse(accessToken, tokenProperties.getValidity());
		} catch (Exception e) {
			log.error("Token creation failed: {}", e.getMessage());
			throw new TokenCreationException(ErrorCode.TOKEN_CREATION_FAILED);
		}
	}

	/**
	 * JWT 토큰에서 사용자명을 추출합니다.
	 *
	 * @param token JWT 토큰
	 * @return 사용자명 (subject)
	 */
	@Override
	public String getSubject(String token) {
		try {
			Claims claims = Jwts.parser()
				.setSigningKey(secretKey) // 서명 키 설정
				.parseClaimsJws(token) // 토큰 파싱
				.getBody(); // Claims 객체 추출

			return claims.getSubject();
		} catch (Exception e) {
			log.error("Failed to parse JWT token: {}", e.getMessage());
			throw new InvalidTokenException(ErrorCode.TOKEN_INVALID);
		}
	}
}