package com.shinhancard.toss.io;

/**
 * JWT 응답 정보를 포함하는 데이터 전송 객체 (DTO)입니다.
 * <p>
 * 서버에서 클라이언트에게 JWT 응답을 보낼 때 필요한 정보를 담고 있습니다.
 * 이 클래스는 불변 객체로, 모든 필드는 final로 설정되어 있습니다.
 * </p>
 *
 * @param tokenType             토큰 유형 (예: "bearer")
 * @param accessToken           액세스 토큰
 * @param expiresIn             액세스 토큰 만료 시간 (초 단위)
 * @param refreshToken          리프레시 토큰
 * @param refreshTokenExpiresIn 리프레시 토큰 만료 시간 (초 단위)
 * @param scope                 토큰의 범위 (예: "account_email profile")
 */
public record JwtResponse(
	String tokenType,
	String accessToken,
	int expiresIn,
	String refreshToken,
	int refreshTokenExpiresIn,
	String scope
) {
}