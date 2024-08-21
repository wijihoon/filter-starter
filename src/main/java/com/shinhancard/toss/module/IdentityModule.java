package com.shinhancard.toss.module;

import java.util.function.Function;

import com.shinhancard.toss.io.LagacyBody;
import com.shinhancard.toss.io.TokenResponse;

/**
 * 이 모듈은 LagacyBody 객체에 대한 변환 및 작업을 Function을 사용하여 처리합니다.
 */
public class IdentityModule {

	/**
	 * 문자열을 LagacyBody 객체로 변환합니다.
	 * <p>
	 * 입력 문자열이 null이거나 빈 문자열인 경우 예외를 발생시킵니다.
	 * </p>
	 *
	 * @param input 문자열 입력
	 * @return 변환된 LagacyBody 객체
	 * @throws IllegalArgumentException 입력 문자열이 null이거나 빈 문자열인 경우
	 */
	public final Function<String, LagacyBody> stringToLagacyBody = input -> {
		// 입력이 null이거나 빈 문자열일 경우 예외 발생
		if (input == null || input.isBlank()) {
			throw new IllegalArgumentException("Input string cannot be null or blank.");
		}
		// LagacyBody 객체로 변환하여 반환
		return new LagacyBody(input);
	};

	/**
	 * LagacyBody 객체에 특정 작업을 수행하고 수정된 LagacyBody 객체를 반환합니다.
	 * <p>
	 * 이 예제에서는 LagacyBody의 콘텐츠에 접미사를 추가합니다.
	 * </p>
	 *
	 * @param lagacyBody 작업을 수행할 LagacyBody 객체
	 * @return 수정된 LagacyBody 객체
	 * @throws IllegalArgumentException LagacyBody 객체가 null인 경우
	 */
	public final Function<LagacyBody, LagacyBody> performOperation = lagacyBody -> {
		// LagacyBody가 null인 경우 예외 발생
		if (lagacyBody == null) {
			throw new IllegalArgumentException("LagacyBody cannot be null.");
		}
		// LagacyBody의 콘텐츠에 접미사를 추가하여 수정
		String newContent = lagacyBody.getContent() + " - Processed";
		lagacyBody.setContent(newContent);
		// 수정된 LagacyBody 객체를 반환
		return lagacyBody;
	};

	/**
	 * LagacyBody 객체를 TokenResponse 객체로 변환합니다.
	 * <p>
	 * 이 예제에서는 LagacyBody의 콘텐츠를 토큰으로 사용합니다.
	 * </p>
	 *
	 * @param lagacyBody 변환할 LagacyBody 객체
	 * @return 변환된 TokenResponse 객체
	 * @throws IllegalArgumentException LagacyBody 객체가 null인 경우
	 */
	public final Function<LagacyBody, TokenResponse> lagacyBodyToTokenResponse = lagacyBody -> {
		// LagacyBody가 null인 경우 예외 발생
		if (lagacyBody == null) {
			throw new IllegalArgumentException("LagacyBody cannot be null.");
		}
		// LagacyBody의 콘텐츠를 토큰으로 사용하여 TokenResponse 객체 생성
		String token = lagacyBody.getContent();
		return new TokenResponse(token, 0);
	};
}