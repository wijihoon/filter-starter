package com.shinhancard.toss.service;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.io.TokenResponse;

/**
 * 인증 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 사용자의 인증 및 CI 처리와 관련된 메소드를 제공합니다.
 * </p>
 */
public interface AuthService {

	/**
	 * 암호화된 CI를 Base64로 복호화합니다.
	 *
	 * @param encryptedCi 암호화된 CI
	 * @return 복호화된 CI
	 * @throws AuthenticationException 복호화 실패 시 발생하는 예외
	 */
	String decryptCi(String encryptedCi) throws AuthenticationException;

	/**
	 * 사용자 CI로 고객 정보를 조회합니다.
	 *
	 * @param decryptCi 복호화된 CI
	 * @return 고객 정보를 포함한 객체
	 * @throws AuthenticationException 고객 정보 조회 실패 시 발생하는 예외
	 */
	TokenResponse getCustomerInfo(String decryptCi) throws AuthenticationException;
}
