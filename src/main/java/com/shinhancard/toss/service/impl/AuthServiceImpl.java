package com.shinhancard.toss.service.impl;

import java.util.Base64;

import org.springframework.stereotype.Service;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.io.TokenResponse;
import com.shinhancard.toss.module.IdentityModule;
import com.shinhancard.toss.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * <p>
 * 이 클래스는 사용자의 인증, 토큰 생성, 쿠키에서 토큰 추출 및 토큰 검증을 수행합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	/**
	 * 암호화된 CI를 Base64로 복호화합니다.
	 *
	 * @param encryptedCi 암호화된 CI
	 * @return 복호화된 CI
	 * @throws AuthenticationException 복호화 실패 시 발생하는 예외
	 */
	@Override
	public String decryptCi(String encryptedCi) {
		try {
			return new String(Base64.getDecoder().decode(encryptedCi)); // Base64 복호화 수행
		} catch (IllegalArgumentException e) {
			log.error("Failed to decode CI: {}", encryptedCi, e); // 복호화 실패 시 오류 로그 출력
			throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS); // 예외 발생
		}
	}

	/**
	 * 사용자 CI로 고객 정보를 받는 메소드
	 *
	 * @param encryptedCi 복호화된 CI
	 * @return 유효한 사용자이면 TokenResponse 객체, 그렇지 않으면 예외 발생
	 */
	@Override
	public TokenResponse getCustomerInfo(String encryptedCi) {
		IdentityModule identityModule = new IdentityModule();
		// IdentityModule을 사용하여 사용자 검증
		return identityModule.stringToLagacyBody
			.andThen(identityModule.performOperation)
			.andThen(identityModule.lagacyBodyToTokenResponse)
			.apply(encryptedCi);
	}
}
