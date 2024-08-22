package com.shinhancard.toss.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.io.ResponseVo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom CSRF 필터 클래스입니다. 이 필터는 요청의 CSRF 토큰을 검증하고 위반된 경우
 * 적절한 에러 응답을 처리합니다.
 */
@Slf4j
public class CustomCsrfFilter extends OncePerRequestFilter {

	private static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";
	private static final List<String> EXCLUDED_PATHS = Arrays.asList("/public/**", "/api/public/**");

	/**
	 * 요청의 CSRF 토큰을 검증하고, 위반된 경우 예외를 처리합니다.
	 *
	 * @param request     HttpServletRequest 객체
	 * @param response    HttpServletResponse 객체
	 * @param filterChain 필터 체인
	 * @throws ServletException Servlet 처리 예외
	 * @throws IOException      입출력 예외
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		// 공개 경로는 CSRF 검증을 건너뜁니다.
		if (isExcludedPath(request.getRequestURI())) {
			log.debug("공개 경로 접근: CSRF 검증을 건너뜁니다.");
			filterChain.doFilter(request, response);
			return;
		}

		// CSRF 토큰 검증
		String csrfToken = request.getHeader(CSRF_HEADER_NAME);
		String sessionCsrfToken = (String)request.getSession().getAttribute(CSRF_HEADER_NAME);

		if (csrfToken == null || sessionCsrfToken == null || !Objects.equals(csrfToken, sessionCsrfToken)) {
			log.warn("CSRF 검증 실패: 잘못된 CSRF 토큰입니다.");
			handleCsrfViolation(response, ErrorCode.CSRF_TOKEN_INVALID);
			return;
		}

		// CSRF 검증이 완료된 후 필터 체인 계속 진행
		log.debug("CSRF 검증 완료. 필터 체인을 계속 진행합니다.");
		filterChain.doFilter(request, response);
	}

	/**
	 * 요청 경로가 CSRF 검증을 제외할 경로인지 확인합니다.
	 *
	 * @param requestPath 요청 경로
	 * @return 경로가 제외된 경우 true, 그렇지 않으면 false
	 */
	private boolean isExcludedPath(String requestPath) {
		return EXCLUDED_PATHS.stream().anyMatch(requestPath::matches);
	}

	/**
	 * CSRF 정책 위반 시 에러 응답을 처리합니다.
	 *
	 * @param response   HttpServletResponse 객체
	 * @param errorCode  에러 코드
	 * @throws IOException 입출력 예외
	 */
	private void handleCsrfViolation(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		log.error("CSRF 정책 위반: {}", errorCode.getMessage());
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json");
		try (var writer = response.getWriter()) {
			writer.write(
				ResponseVo.error(errorCode.getMessage(), errorCode.getHttpStatus()).toString()
			);
			writer.flush();
		}
	}
}
