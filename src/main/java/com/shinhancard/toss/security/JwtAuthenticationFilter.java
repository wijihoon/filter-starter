package com.shinhancard.toss.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.exception.JwtTokenException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 인증 필터 클래스입니다.
 * <p>
 * HTTP 요청에서 JWT 토큰을 추출하고 인증을 수행합니다.
 * </p>
 */
@Slf4j // 로그를 위한 Lombok 어노테이션
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final JwtTokenService jwtTokenService; // JWT 토큰 서비스 (final로 선언하여 안전하게 사용)

	/**
	 * 생성자입니다.
	 * <p>
	 * {@link JwtTokenService}를 주입받아 필터를 초기화합니다.
	 * </p>
	 *
	 * @param defaultFilterProcessesUrl 필터가 처리할 URL 패턴
	 * @param jwtTokenService           JWT 토큰 서비스
	 */
	public JwtAuthenticationFilter(String defaultFilterProcessesUrl, JwtTokenService jwtTokenService) {
		super(defaultFilterProcessesUrl);
		this.jwtTokenService = jwtTokenService;
	}

	/**
	 * 인증 시도를 수행합니다.
	 * <p>
	 * 요청에서 JWT 토큰을 추출하고, 유효성을 검사하여 사용자 인증 정보를 생성합니다.
	 * </p>
	 *
	 * @param request  HTTP 요청
	 * @param response HTTP 응답
	 * @return 인증 정보
	 * @throws IOException      입출력 예외
	 * @throws ServletException 서블릿 예외
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		String token = extractToken(request); // 요청에서 JWT 토큰 추출

		if (token != null) {
			if (jwtTokenService.validateToken(token)) {
				// JWT 토큰이 유효한 경우, 사용자 이름을 추출하여 인증 정보를 생성합니다.
				return jwtTokenService.getAuthentication(token);
			} else {
				log.warn("Invalid JWT Token"); // 토큰이 유효하지 않은 경우 로그 기록
				throw new JwtTokenException(ErrorCode.JWT_TOKEN_INVALID); // 토큰이 유효하지 않은 경우 예외 처리
			}
		} else {
			log.warn("JWT Token Missing"); // 토큰이 없는 경우 로그 기록
			throw new JwtTokenException(ErrorCode.JWT_TOKEN_MISSING); // 토큰이 없는 경우 예외 처리
		}
	}

	/**
	 * 요청에서 JWT 토큰을 추출합니다.
	 * <p>
	 * HTTP 헤더에서 "Authorization" 헤더를 찾고, "Bearer " 접두사를 제거하여 토큰을 반환합니다.
	 * </p>
	 *
	 * @param request HTTP 요청
	 * @return JWT 토큰 또는 null
	 */
	private String extractToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization"); // 요청 헤더에서 Authorization 가져오기
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7); // "Bearer " 접두사 제거
		}
		return null; // 토큰이 없는 경우
	}

	/**
	 * 인증 성공 후 호출됩니다.
	 * <p>
	 * 인증 정보를 SecurityContext에 설정하고 필터 체인을 계속 진행합니다.
	 * </p>
	 *
	 * @param request    HTTP 요청
	 * @param response   HTTP 응답
	 * @param chain      필터 체인
	 * @param authResult 인증 결과
	 * @throws IOException      입출력 예외
	 * @throws ServletException 서블릿 예외
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain, Authentication authResult)
		throws IOException, ServletException {
		SecurityContextHolder.getContext().setAuthentication(authResult); // 인증 정보를 SecurityContext에 설정
		chain.doFilter(request, response); // 필터 체인 계속 진행
	}

	/**
	 * 인증 실패 시 호출됩니다.
	 * <p>
	 * 인증 실패 정보를 클라이언트에게 반환합니다.
	 * </p>
	 *
	 * @param request  HTTP 요청
	 * @param response HTTP 응답
	 * @param failed   인증 실패 예외
	 * @throws IOException      입출력 예외
	 * @throws ServletException 서블릿 예외
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed)
		throws IOException, ServletException {
		log.error("Authentication Failed: {}", failed.getMessage()); // 인증 실패 로그
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.getMessage()); // 인증 실패 시 401 상태 코드와 메시지 반환
	}
}