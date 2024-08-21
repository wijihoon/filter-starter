package com.shinhancard.toss.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JWT 응답 정보를 포함하는 데이터 전송 객체 (DTO)입니다.
 * <p>
 * 서버에서 클라이언트에게 JWT 응답을 보낼 때 필요한 정보를 담고 있습니다.
 * 이 클래스는 불변 객체로 설계되었으며, 모든 필드는 final로 설정되어 있습니다.
 * </p>
 *
 * @param tokenType             토큰 유형 (예: "Bearer"). 기본값은 "Bearer"입니다.
 * @param accessToken           액세스 토큰. 클라이언트가 API 접근 시 사용합니다.
 * @param expiresIn             액세스 토큰 만료 시간 (초 단위).
 */
public record TokenResponse(
	String tokenType,
	String accessToken,
	int expiresIn
) {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 기본 생성자: tokenType의 기본값을 "Bearer"로 설정합니다.
	 *
	 * @param accessToken           액세스 토큰
	 * @param expiresIn             액세스 토큰 만료 시간
	 */
	public TokenResponse(
		String accessToken,
		int expiresIn
	) {
		this("Bearer", accessToken, expiresIn);
	}

	/**
	 * 필드 값이 null 또는 유효하지 않은 경우 예외를 던질 수 있습니다.
	 *
	 * @param tokenType             토큰 유형
	 * @param accessToken           액세스 토큰
	 * @param expiresIn             액세스 토큰 만료 시간
	 */
	public TokenResponse(
		String tokenType,
		String accessToken,
		int expiresIn
	) {
		if (accessToken == null || accessToken.isBlank()) {
			throw new IllegalArgumentException("액세스 토큰은 비어 있을 수 없습니다.");
		}
		this.tokenType = tokenType;
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
	}

	/**
	 * 객체를 JSON 형식의 문자열로 반환합니다.
	 * <p>
	 * 이 메서드는 객체의 상태를 JSON 형식으로 직렬화하여 디버깅 또는 로깅에 활용할 수 있도록 합니다.
	 * 변환 과정에서 문제가 발생할 경우 런타임 예외가 발생합니다.
	 * </p>
	 *
	 * @return JSON 형식의 문자열 표현
	 * @throws RuntimeException JSON 변환 중 예외가 발생한 경우
	 */
	@Override
	public String toString() {
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("객체를 JSON으로 변환하는 데 실패했습니다.", e);
		}
	}
}