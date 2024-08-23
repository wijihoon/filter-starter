package shinhancard.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import shinhancard.io.ErrorCode;
import shinhancard.io.ResponseVo;

/**
 * Custom CORS 필터 클래스입니다. 이 필터는 요청의 CORS 정책을 검증하고 위반된 경우
 * 적절한 에러 응답을 처리합니다.
 */
@Slf4j
public class CorsFilter extends OncePerRequestFilter {

	private final CorsConfigurationSource corsConfigurationSource;

	@Autowired
	public CorsFilter(CorsConfigurationSource corsConfigurationSource) {
		this.corsConfigurationSource = corsConfigurationSource;
	}

	/**
	 * 요청이 CORS 정책을 위반하는지 확인하고, 위반된 경우 예외를 처리합니다.
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

		// 요청의 CORS 헤더를 로깅
		log.debug("처리 중인 요청의 CORS 관련 헤더: Origin={}, Method={}, Headers={}",
			request.getHeader(HttpHeaders.ORIGIN),
			request.getMethod(),
			Collections.list(request.getHeaderNames()));

		// CORS 요청인지 확인합니다.
		if (CorsUtils.isCorsRequest(request)) {
			log.debug("CORS 요청으로 인식되었습니다.");

			// CORS 설정을 가져옵니다.
			CorsConfiguration corsConfig = corsConfigurationSource.getCorsConfiguration(request);

			if (corsConfig == null) {
				log.debug("CORS 설정이 없습니다. 필터 체인 계속 진행.");
				// CORS 설정이 없는 경우, 필터 체인 진행
				filterChain.doFilter(request, response);
				return;
			}

			if (isAllowAllCorsConfig(corsConfig)) {
				log.debug("모든 출처, 메서드, 헤더를 허용하는 CORS 설정입니다. 필터 체인 계속 진행.");
				// 모든 출처, 메서드, 헤더를 허용하는 경우
				filterChain.doFilter(request, response);
				return;
			}

			if (!isOriginAllowed(request.getHeader(HttpHeaders.ORIGIN), corsConfig.getAllowedOrigins())) {
				// 출처가 허용되지 않는 경우
				log.error("CORS 출처 정책 위반: 허용되지 않은 출처입니다. 요청 출처: {}", request.getHeader(HttpHeaders.ORIGIN));
				handleCorsViolation(response, ErrorCode.CORS_ORIGIN_POLICY_VIOLATION);
				return;
			}

			if (!isMethodAllowed(request.getMethod(), corsConfig.getAllowedMethods())) {
				// HTTP 메서드가 허용되지 않는 경우
				log.error("CORS 메서드 정책 위반: 허용되지 않은 메서드입니다. 요청 메서드: {}", request.getMethod());
				handleCorsViolation(response, ErrorCode.CORS_METHOD_POLICY_VIOLATION);
				return;
			}

			if (!areHeadersAllowed(request, corsConfig.getAllowedHeaders())) {
				// 요청 헤더가 허용되지 않는 경우
				log.error("CORS 헤더 정책 위반: 허용되지 않은 헤더입니다. 요청 헤더: {}", Collections.list(request.getHeaderNames()));
				handleCorsViolation(response, ErrorCode.CORS_HEADERS_POLICY_VIOLATION);
				return;
			}
		}

		// CORS 검증이 완료된 후, 필터 체인 계속 진행
		log.debug("CORS 검증 완료. 필터 체인을 계속 진행합니다.");
		filterChain.doFilter(request, response);
	}

	/**
	 * CORS 설정이 모든 출처, 메서드, 헤더를 허용하는지 확인합니다.
	 *
	 * @param corsConfig CORS 설정
	 * @return 모든 CORS 요청을 허용하는 경우 true, 그렇지 않으면 false
	 */
	private boolean isAllowAllCorsConfig(CorsConfiguration corsConfig) {
		boolean allOriginsAllowed = Objects.requireNonNull(corsConfig.getAllowedOrigins())
			.contains(CorsConfiguration.ALL);
		boolean allMethodsAllowed = Objects.requireNonNull(corsConfig.getAllowedMethods())
			.contains(CorsConfiguration.ALL);
		boolean allHeadersAllowed = Objects.requireNonNull(corsConfig.getAllowedHeaders())
			.contains(CorsConfiguration.ALL);
		return allOriginsAllowed || allMethodsAllowed || allHeadersAllowed;
	}

	/**
	 * 요청 출처(origin)가 허용된 목록에 포함되어 있는지 확인합니다.
	 *
	 * @param origin         요청 출처
	 * @param allowedOrigins 허용된 출처 목록
	 * @return 출처가 허용된 경우 true, 그렇지 않으면 false
	 */
	private boolean isOriginAllowed(String origin, List<String> allowedOrigins) {
		if (allowedOrigins == null || allowedOrigins.isEmpty() || allowedOrigins.contains(CorsConfiguration.ALL)) {
			return true;
		}
		UriComponents originComponents = UriComponentsBuilder.fromUriString(origin).build();
		boolean isAllowed = allowedOrigins.stream()
			.map(UriComponentsBuilder::fromUriString)
			.map(UriComponentsBuilder::build)
			.anyMatch(allowedComponents -> compareUriComponents(originComponents, allowedComponents));

		log.debug("CORS 출처 검증 결과: 요청 출처={}, 허용된 출처 목록={}, 검증 결과={}",
			origin, allowedOrigins, isAllowed);
		return isAllowed;
	}

	/**
	 * 요청의 HTTP 메서드가 허용된 목록에 포함되어 있는지 확인합니다.
	 *
	 * @param method         요청 메서드
	 * @param allowedMethods 허용된 메서드 목록
	 * @return 메서드가 허용된 경우 true, 그렇지 않으면 false
	 */
	private boolean isMethodAllowed(String method, List<String> allowedMethods) {
		boolean isAllowed = allowedMethods == null || allowedMethods.isEmpty()
			|| allowedMethods.contains(CorsConfiguration.ALL)
			|| allowedMethods.contains(method);

		log.debug("CORS 메서드 검증 결과: 요청 메서드={}, 허용된 메서드 목록={}, 검증 결과={}",
			method, allowedMethods, isAllowed);
		return isAllowed;
	}

	/**
	 * 요청의 모든 헤더가 허용된 헤더 목록에 포함되어 있는지 확인합니다.
	 *
	 * @param request       HttpServletRequest 객체
	 * @param allowedHeaders 허용된 헤더 목록
	 * @return 모든 헤더가 허용된 경우 true, 그렇지 않으면 false
	 */
	private boolean areHeadersAllowed(HttpServletRequest request, List<String> allowedHeaders) {
		if (allowedHeaders == null || allowedHeaders.isEmpty()) {
			return true;
		}
		Set<String> allowedHeaderSet = new HashSet<>(allowedHeaders);
		Enumeration<String> requestHeaderNames = request.getHeaderNames();
		boolean areHeadersAllowed = requestHeaderNames == null || Collections.list(requestHeaderNames).stream()
			.map(String::trim)
			.allMatch(allowedHeaderSet::contains);

		log.debug("CORS 헤더 검증 결과: 요청 헤더={}, 허용된 헤더 목록={}, 검증 결과={}",
			Collections.list(requestHeaderNames), allowedHeaders, areHeadersAllowed);
		return areHeadersAllowed;
	}

	/**
	 * 두 URI 컴포넌트를 비교하여 동일한지 확인합니다.
	 *
	 * @param origin  요청 출처의 URI 컴포넌트
	 * @param allowed 허용된 출처의 URI 컴포넌트
	 * @return URI 컴포넌트가 동일한 경우 true, 그렇지 않으면 false
	 */
	private boolean compareUriComponents(UriComponents origin, UriComponents allowed) {
		return Objects.equals(origin.getScheme(), allowed.getScheme()) &&
			Objects.equals(origin.getHost(), allowed.getHost()) &&
			(origin.getPort() == -1 ? allowed.getPort() == -1 : origin.getPort() == allowed.getPort());
	}

	/**
	 * CORS 정책 위반 시 에러 응답을 처리합니다.
	 *
	 * @param response   HttpServletResponse 객체
	 * @param errorCode  에러 코드
	 * @throws IOException 입출력 예외
	 */
	private void handleCorsViolation(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		log.error("CORS 정책 위반: {}", errorCode.getMessage());
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
