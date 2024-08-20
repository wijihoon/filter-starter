package com.shinhancard.toss.io;

import jakarta.validation.constraints.NotBlank;

/**
 * JWT 인증 요청을 위한 데이터 전송 객체 (DTO)입니다.
 * <p>
 * 클라이언트가 서버에 JWT 인증 요청을 보낼 때 필요한 필드를 담고 있습니다.
 * 이 클래스는 불변 객체로, 각 필드는 요청의 유효성을 검증하기 위해 @NotBlank 어노테이션을 사용하여 필수 입력 항목을 명시합니다.
 * </p>
 *
 * @param grantType    인증 요청의 그랜트 타입, 고정값으로 "authorization_code"가 사용됩니다. (필수)
 * @param clientId     애플리케이션의 REST API 키입니다. (필수)
 * @param redirectUrl  인가 코드가 리다이렉트된 URL입니다. (필수)
 * @param code         인가 코드 받기 요청으로 얻은 인가 코드입니다. (필수)
 * @param clientSecret 토큰 발급 시 보안을 강화하기 위해 추가 확인하는 코드입니다. (선택적)
 */
public record JwtRequest(
	@NotBlank(message = "그랜트 타입은 비어 있을 수 없습니다") String grantType,
	@NotBlank(message = "클라이언트 ID는 비어 있을 수 없습니다") String clientId,
	@NotBlank(message = "리다이렉트 URL은 비어 있을 수 없습니다") String redirectUrl,
	@NotBlank(message = "인가 코드는 비어 있을 수 없습니다") String code,
	String clientSecret
) {
}