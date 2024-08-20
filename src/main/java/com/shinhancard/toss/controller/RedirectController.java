package com.shinhancard.toss.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 리다이렉션 관련 요청을 처리하는 컨트롤러입니다.
 * <p>
 * 쿠키에 담긴 토큰을 검증하고 유효할 경우 지정된 URL로 리다이렉션합니다.
 * </p>
 */
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Redirect", description = "리다이렉션 관련 API")
public class RedirectController {

	// AuthService 주입
	private final AuthService authService;

	/**
	 * 쿠키에 담긴 토큰을 검증하고 리다이렉션합니다.
	 *
	 * @param request  HTTP 요청 객체
	 * @param response HTTP 응답 객체
	 * @throws IOException 입출력 예외
	 */
	@Operation(
		summary = "토큰 검증 및 리다이렉션",
		description = "쿠키에 담긴 토큰을 검증하고 유효할 경우 리다이렉션합니다.",
		responses = {
			@ApiResponse(responseCode = "302", description = "리다이렉션 성공"),
			@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
		}
	)
	@GetMapping("/redirect")
	public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			// 쿠키에서 토큰 추출
			String token = authService.getTokenFromCookie(request);

			// 토큰 검증
			if (token == null || !authService.validateToken(token)) {
				// 유효하지 않은 토큰 처리
				throw new AuthenticationException(ErrorCode.TOKEN_INVALID);
			}

			// 리다이렉션 수행
			response.sendRedirect("https://other-domain.com/success");
		} catch (AuthenticationException e) {
			// 인증 실패 로그 기록 및 HTTP 401 응답 반환
			log.warn("Authentication failed: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (IOException e) {
			// 리다이렉션 실패 로그 기록 및 IOException 재던지기
			log.error("Redirection failed: {}", e.getMessage());
			throw e; // IOException을 다시 던져서 Spring에서 처리
		}
	}
}
