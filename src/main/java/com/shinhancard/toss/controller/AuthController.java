package com.shinhancard.toss.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.exception.InvalidRedirectUrlException;
import com.shinhancard.toss.io.JwtRequest;
import com.shinhancard.toss.io.JwtResponse;
import com.shinhancard.toss.io.ResponseVo;
import com.shinhancard.toss.service.AuthService;
import com.shinhancard.toss.util.CookieUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 관련 요청을 처리하는 컨트롤러입니다.
 * <p>
 * {@link AuthService}를 사용하여 사용자 인증을 처리하고, 인증 결과를 쿠키나 헤더, 바디에 반환합니다.
 * </p>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication operations")
public class AuthController {

	// 인증 서비스 주입
	private final AuthService authService;

	/**
	 * 사용자를 인증하고, JWT 토큰을 쿠키에 담아 지정된 URL로 리다이렉트합니다.
	 *
	 * @param jwtRequest 사용자 인증 요청 정보
	 * @param response   HTTP 응답
	 * @return {@link ResponseVo} 성공 또는 오류 응답
	 * @throws IOException I/O 예외
	 */
	@PostMapping("/login-and-redirect")
	@Operation(
		summary = "Authenticate user and redirect",
		description = "Authenticate user with encrypted CI and redirect to the specified URL with JWT token in a cookie.",
		responses = {
			@ApiResponse(responseCode = "200", description = "Successful authentication and redirection"),
			@ApiResponse(responseCode = "400", description = "Invalid or missing redirect URL"),
			@ApiResponse(responseCode = "401", description = "Authentication failed")
		}
	)
	public ResponseVo<Void> authenticateUserAndRedirect(
		@RequestBody JwtRequest jwtRequest, HttpServletResponse response) throws IOException {

		try {
			// 사용자 인증
			JwtResponse token = authService.authenticateWithTokens(jwtRequest);

			// JWT 토큰을 쿠키에 저장
			Cookie jwtCookie = CookieUtil.createCookie("JWT", token.accessToken());
			response.addCookie(jwtCookie);

			// 리다이렉트 URL 검증
			String redirectUrl = jwtRequest.redirectUrl();
			if (redirectUrl != null && !redirectUrl.isEmpty()) {
				response.sendRedirect(redirectUrl);
				return ResponseVo.success(null);
			} else {
				throw new InvalidRedirectUrlException(ErrorCode.INVALID_REDIRECT_URL);
			}
		} catch (InvalidRedirectUrlException e) {
			// 리다이렉트 URL 오류 처리
			log.error("Redirect URL error: {}", e.getErrorMessage());
			response.sendError(e.getHttpStatus().value(), e.getErrorMessage());
			return ResponseVo.error(e.getErrorMessage(), e.getHttpStatus());
		} catch (AuthenticationException e) {
			// 인증 실패 처리
			log.error("Authentication failed: {}", e.getErrorMessage());
			response.sendError(e.getHttpStatus().value(), e.getErrorMessage());
			return ResponseVo.error(e.getErrorMessage(), e.getHttpStatus());
		}
	}

	/**
	 * 사용자를 인증하고 JWT 토큰을 쿠키에 담아 반환합니다.
	 *
	 * @param jwtRequest 사용자 인증 요청 정보
	 * @param response   HTTP 응답
	 * @return {@link ResponseVo} 성공 또는 오류 응답
	 */
	@PostMapping("/login-with-cookie")
	@Operation(
		summary = "Authenticate user with cookie",
		description = "Authenticate user with encrypted CI and return JWT token in a cookie.",
		responses = {
			@ApiResponse(responseCode = "200", description = "Successful authentication with JWT token in cookie"),
			@ApiResponse(responseCode = "401", description = "Authentication failed")
		}
	)
	public ResponseVo<String> authenticateUserWithCookie(
		@RequestBody JwtRequest jwtRequest, HttpServletResponse response) {

		try {
			// 사용자 인증
			JwtResponse token = authService.authenticateWithTokens(jwtRequest);

			// JWT 토큰을 쿠키에 저장
			Cookie jwtCookie = CookieUtil.createCookie("JWT", token.accessToken());
			response.addCookie(jwtCookie);
			return ResponseVo.success("Authentication successful");
		} catch (AuthenticationException e) {
			// 인증 실패 처리
			log.error("Authentication failed: {}", e.getErrorMessage());
			return ResponseVo.error(e.getErrorMessage(), e.getHttpStatus());
		}
	}

	/**
	 * 사용자를 인증하고 JWT 토큰을 응답 헤더에 담아 반환합니다.
	 *
	 * @param jwtRequest 사용자 인증 요청 정보
	 * @param response   HTTP 응답
	 * @return {@link ResponseVo} 성공 또는 오류 응답
	 */
	@PostMapping("/login-with-header")
	@Operation(
		summary = "Authenticate user with header",
		description = "Authenticate user with encrypted CI and return JWT token in response header.",
		responses = {
			@ApiResponse(responseCode = "200", description = "Successful authentication with JWT token in header"),
			@ApiResponse(responseCode = "401", description = "Authentication failed")
		}
	)
	public ResponseVo<String> authenticateUserWithHeader(
		@RequestBody JwtRequest jwtRequest, HttpServletResponse response) {

		try {
			// 사용자 인증
			JwtResponse token = authService.authenticateWithTokens(jwtRequest);

			// JWT 토큰을 응답 헤더에 저장
			response.setHeader("Authorization", "Bearer " + token.accessToken());
			return ResponseVo.success("Authentication successful");
		} catch (AuthenticationException e) {
			// 인증 실패 처리
			log.error("Authentication failed: {}", e.getErrorMessage());
			return ResponseVo.error(e.getErrorMessage(), e.getHttpStatus());
		}
	}

	/**
	 * 사용자를 인증하고 JWT 토큰을 응답 바디에 담아 반환합니다.
	 *
	 * @param jwtRequest 사용자 인증 요청 정보
	 * @return {@link ResponseVo} 성공 또는 오류 응답
	 */
	@PostMapping("/login-with-body")
	@Operation(
		summary = "Authenticate user with body",
		description = "Authenticate user with encrypted CI and return JWT token in response body.",
		responses = {
			@ApiResponse(responseCode = "200", description = "Successful authentication with JWT token in response body"),
			@ApiResponse(responseCode = "401", description = "Authentication failed")
		}
	)
	public ResponseVo<JwtResponse> authenticateUserWithBody(@RequestBody JwtRequest jwtRequest) {

		try {
			// 사용자 인증
			JwtResponse jwtResponse = authService.authenticateWithTokens(jwtRequest);
			return ResponseVo.success(jwtResponse);
		} catch (AuthenticationException e) {
			// 인증 실패 처리
			log.error("Authentication failed: {}", e.getErrorMessage());
			return ResponseVo.error(e.getErrorMessage(), e.getHttpStatus());
		}
	}
}