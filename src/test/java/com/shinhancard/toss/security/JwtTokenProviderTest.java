package com.shinhancard.toss.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.shinhancard.toss.exception.InvalidTokenException;
import com.shinhancard.toss.exception.TokenCreationException;
import com.shinhancard.toss.exception.TokenRefreshException;
import com.shinhancard.toss.properties.JwtTokenProperties;
import com.shinhancard.toss.service.RedisService;

import io.jsonwebtoken.security.Keys;

/**
 * JwtTokenProvider 클래스의 기능을 테스트하기 위한 단위 테스트 클래스입니다.
 * <p>
 * 이 클래스는 JWT 토큰 생성, 갱신, 유효성 검증 등의 로직을 테스트합니다.
 * </p>
 */
class JwtTokenProviderTest {

	// RedisService를 Mock 객체로 설정하여 테스트 환경에서 Redis를 대신함
	@Mock
	private RedisService redisService;

	// JwtTokenProperties를 Mock 객체로 설정
	@Mock
	private JwtTokenProperties jwtTokenProperties;

	// 실제 테스트할 대상 객체
	@InjectMocks
	private JwtTokenProvider jwtTokenProvider;

	/**
	 * 각 테스트가 실행되기 전에 설정 작업을 수행합니다.
	 * MockitoAnnotations를 통해 Mock 객체들을 초기화하고, SecretKey를 설정합니다.
	 */
	@BeforeEach
	void setUp() {
		// Mock 객체 초기화
		MockitoAnnotations.openMocks(this);

		// 비밀 키를 생성
		// JWT 서명에 사용할 SecretKey
		SecretKey secretKey = Keys.hmacShaKeyFor("test-secret-key".getBytes());

		// JwtTokenProvider 객체를 수동으로 초기화
		jwtTokenProvider = new JwtTokenProvider(redisService, jwtTokenProperties);
	}

	/**
	 * JWT 토큰 생성 로직을 테스트합니다.
	 * 정상적으로 토큰이 생성되는지 검증합니다.
	 */
	@Test
	@DisplayName("유효한 토큰 생성 테스트")
	void testCreateToken() {
		// JwtTokenProperties의 메서드들이 정상적으로 동작하도록 설정
		when(jwtTokenProperties.getSecret()).thenReturn("test-secret-key");
		when(jwtTokenProperties.getValidity()).thenReturn(3600000L); // 1시간 유효

		// 토큰 생성
		String token = jwtTokenProvider.createToken("testUser");

		// 토큰이 null이 아닌지 확인
		assertNotNull(token);
	}

	/**
	 * 토큰 생성 실패 시의 로직을 테스트합니다.
	 * 비밀 키 생성에 실패할 때 TokenCreationException이 발생하는지 검증합니다.
	 */
	@Test
	@DisplayName("토큰 생성 실패 테스트")
	void testCreateTokenFailure() {
		// 비밀 키 생성 실패를 시뮬레이션
		when(jwtTokenProperties.getSecret()).thenThrow(new RuntimeException("키 생성 실패"));

		// TokenCreationException이 발생하는지 확인
		assertThrows(TokenCreationException.class, () -> jwtTokenProvider.createToken("testUser"));
	}

	/**
	 * 리프레시 토큰 생성 로직을 테스트합니다.
	 * 정상적으로 리프레시 토큰이 생성되는지 검증합니다.
	 */
	@Test
	@DisplayName("유효한 리프레시 토큰 생성 테스트")
	void testCreateRefreshToken() {
		// JwtTokenProperties의 메서드들이 정상적으로 동작하도록 설정
		when(jwtTokenProperties.getSecret()).thenReturn("test-secret-key");
		when(jwtTokenProperties.getRefreshValidity()).thenReturn(7200000L); // 2시간 유효

		// 리프레시 토큰 생성
		String refreshToken = jwtTokenProvider.createRefreshToken("testUser");

		// 리프레시 토큰이 null이 아닌지 확인
		assertNotNull(refreshToken);
	}

	/**
	 * 기존의 액세스 토큰을 갱신하는 로직을 테스트합니다.
	 * 갱신이 성공적으로 이루어지며 새 토큰이 생성되는지 검증합니다.
	 */
	@Test
	@DisplayName("토큰 갱신 성공 테스트")
	void testRefreshTokenSuccess() {
		// 기존 토큰 생성
		String oldToken = jwtTokenProvider.createToken("testUser");

		// Redis에 기존 토큰이 존재한다고 가정
		when(redisService.isMemberOfSet("testUser:tokens", oldToken)).thenReturn(true);

		// 토큰 갱신
		String newToken = jwtTokenProvider.refreshToken(oldToken);

		// 새로 생성된 토큰이 null이 아닌지 확인
		assertNotNull(newToken);

		// Redis에서 기존 토큰이 제거되었는지 확인
		verify(redisService).removeFromSet("testUser:tokens", oldToken);
	}

	/**
	 * 유효하지 않은 토큰으로 갱신을 시도할 때 실패하는지 테스트합니다.
	 * TokenRefreshException이 발생하는지 검증합니다.
	 */
	@Test
	@DisplayName("유효하지 않은 토큰으로 갱신 실패 테스트")
	void testRefreshTokenFailure() {
		// 유효하지 않은 토큰 설정
		String invalidToken = "invalidToken";

		// Redis에 해당 토큰이 존재하지 않는다고 가정
		when(redisService.isMemberOfSet("testUser:tokens", invalidToken)).thenReturn(false);

		// TokenRefreshException이 발생하는지 확인
		assertThrows(TokenRefreshException.class, () -> jwtTokenProvider.refreshToken(invalidToken));
	}

	/**
	 * JWT 토큰의 유효성 검증 로직을 테스트합니다.
	 * 유효한 토큰이 정상적으로 검증되는지 확인합니다.
	 */
	@Test
	@DisplayName("토큰 유효성 검증 성공 테스트")
	void testValidateTokenSuccess() {
		// 유효한 토큰 생성
		String validToken = jwtTokenProvider.createToken("testUser");

		// Redis에 해당 토큰이 존재한다고 가정
		when(redisService.isMemberOfSet("testUser:tokens", validToken)).thenReturn(true);

		// 토큰이 유효한지 확인
		assertTrue(jwtTokenProvider.validateToken(validToken));
	}

	/**
	 * 유효하지 않은 토큰 검증 로직을 테스트합니다.
	 * InvalidTokenException이 발생하는지 확인합니다.
	 */
	@Test
	@DisplayName("유효하지 않은 토큰 검증 실패 테스트")
	void testValidateTokenFailure() {
		// 유효하지 않은 토큰 설정
		String invalidToken = "invalidToken";

		// Redis에 해당 토큰이 존재하지 않는다고 가정
		when(redisService.isMemberOfSet("testUser:tokens", invalidToken)).thenReturn(false);

		// InvalidTokenException이 발생하는지 확인
		assertThrows(InvalidTokenException.class, () -> jwtTokenProvider.validateToken(invalidToken));
	}
}