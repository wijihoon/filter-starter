package com.shinhancard.toss.facade;

import org.springframework.stereotype.Component;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.io.TokenResponse;
import com.shinhancard.toss.service.AuthService;
import com.shinhancard.toss.service.TokenService;

import lombok.RequiredArgsConstructor;

/**
 * 인증 및 JWT 토큰 생성을 위한 Facade 클래스입니다.
 * <p>
 * 이 클래스는 AuthService 및 JwtTokenService의 복잡한 로직을 감추고,
 * 클라이언트에게 간단한 인터페이스를 제공합니다.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class OAuthFacade {

	private final AuthService authService;
	private final TokenService jwtTokenService;

	/**
	 * 암호화된 CI를 기반으로 사용자 인증을 수행하고 JWT 토큰을 생성합니다.
	 *
	 * @param encryptedCi 암호화된 CI
	 * @return 생성된 토큰 응답
	 * @throws AuthenticationException 인증 실패 시 발생할 수 있는 예외
	 */
	public TokenResponse authenticateAndCreateToken(String encryptedCi) throws AuthenticationException {
		// CI 복호화 및 사용자 검증
		String decryptCi = authService.decryptCi(encryptedCi);
		TokenResponse tokenResponse = authService.getCustomerInfo(decryptCi);

		// JWT 토큰 생성
		return jwtTokenService.createToken(tokenResponse);
	}
}
