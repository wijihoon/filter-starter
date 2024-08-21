package com.shinhancard.toss.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;

/**
 * JWT 인증 요청을 위한 데이터 전송 객체 (DTO)입니다.
 * <p>
 * 클라이언트가 서버에 JWT 인증 요청을 보낼 때 필요한 필드를 담고 있습니다.
 * 이 클래스는 불변 객체로, 각 필드는 요청의 유효성을 검증하기 위해 @NotBlank 어노테이션을 사용하여 필수 입력 항목을 명시합니다.
 * </p>
 *
 * @param encryptedCi    인증 요청의 암호화 CI (필수)
 */
public record TokenRequest(
	@NotBlank(message = "암호화 CI는 비어 있을 수 없습니다") String encryptedCi
) {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 객체를 JSON 문자열로 변환하여 반환합니다.
	 * <p>
	 * 이 메서드는 객체의 상태를 JSON 형식으로 직렬화하여 디버깅 또는 로깅에 활용할 수 있도록 합니다.
	 * 변환 과정에서 문제가 발생할 경우 런타임 예외가 발생합니다.
	 * </p>
	 *
	 * @return JSON 형식의 문자열 표현
	 * @throws IllegalStateException JSON 변환 중 예외가 발생한 경우
	 */
	@Override
	public String toString() {
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("JWT 요청 객체를 JSON으로 변환하는 데 실패했습니다.", e);
		}
	}
}