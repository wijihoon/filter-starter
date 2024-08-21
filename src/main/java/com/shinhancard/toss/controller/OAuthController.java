package com.shinhancard.toss.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.facade.OAuthFacade;
import com.shinhancard.toss.io.ResponseVo;
import com.shinhancard.toss.io.TokenRequest;
import com.shinhancard.toss.io.TokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 관련 요청을 처리하는 컨트롤러입니다.
 * <p>
 * {@link OAuthFacade}를 사용하여 사용자 인증을 처리하고, 인증 결과를 쿠키나 헤더, 바디에 반환합니다.
 * </p>
 */
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "인증", description = "인증 관련 작업")
public class OAuthController {

	// 인증 Facade 주입
	private final OAuthFacade authFacade;

	/**
	 * 사용자를 인증하고 JWT 토큰을 응답 헤더에 담아 반환합니다.
	 *
	 * @param tokenRequest 사용자 인증 요청 정보
	 * @param response     HTTP 응답
	 * @return {@link ResponseVo} 성공 또는 오류 응답
	 */
	@PostMapping("/token")
	@Operation(
		summary = "사용자 인증 (헤더에 JWT 토큰 반환)",
		description = "암호화된 CI를 이용해 사용자를 인증하고 JWT 토큰을 응답 헤더에 반환합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "성공적으로 인증되고 JWT 토큰이 헤더에 반환됩니다."),
			@ApiResponse(responseCode = "401", description = "인증 실패")
		}
	)
	public ResponseVo<String> authenticateUserWithHeader(
		@Valid @RequestBody TokenRequest tokenRequest, HttpServletResponse response) {

		try {
			// 사용자 인증 및 JWT 토큰 생성
			TokenResponse tokenResponse = authFacade.authenticateAndCreateToken(tokenRequest.encryptedCi());

			// JWT 토큰을 응답 헤더에 저장
			response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());

			// 성공 응답 반환
			return ResponseVo.success("인증에 성공했습니다.");
		} catch (AuthenticationException e) {
			// 인증 실패 처리
			log.error("인증 실패: {}", e.getErrorMessage());
			return ResponseVo.error(e.getErrorMessage(), e.getHttpStatus());
		} catch (Exception e) {
			log.error("예기치 않은 오류 발생: {}", e.getMessage());
			return ResponseVo.error("서버에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}