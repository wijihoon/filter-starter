package shinhancard.sql.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shinhancard.common.wrapper.WrappedHttpServletRequest;
import shinhancard.sql.properties.SQLInjectionProperties;

/**
 * SQL 인젝션을 방지하는 필터 클래스입니다.
 * <p>
 * 이 필터는 요청 본문, 파라미터 및 쿠키에서 SQL 인젝션 패턴을 검사하여
 * 보안 위협을 방지합니다.
 * </p>
 */
@RequiredArgsConstructor
@Slf4j
public class SQLInjectionFilter extends OncePerRequestFilter {

	private final SQLInjectionProperties sqlInjectionProperties;

	/**
	 * 요청을 필터링하고 SQL 인젝션 패턴을 검사합니다.
	 *
	 * @param request     {@link HttpServletRequest} 객체
	 * @param response    {@link HttpServletResponse} 객체
	 * @param filterChain 필터 체인
	 * @throws ServletException 필터 처리 중 발생할 수 있는 예외
	 * @throws IOException      입출력 처리 중 발생할 수 있는 예외
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		// 요청을 래핑하여 본문을 캐싱합니다.
		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);

		try {
			// SQL 인젝션 검사 수행
			validateRequestParameters(wrappedRequest);
			validateRequestBody(wrappedRequest);
			validateCookies(wrappedRequest);

			// 필터 체인을 계속 진행합니다.
			filterChain.doFilter(wrappedRequest, response);
		} catch (ServletException | RuntimeException e) {
			// 예외 로그 남기기
			log.error("SQL 인젝션 예외 발생: {}", e.getMessage());

			// 적절한 에러 응답 생성
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "부적절한 요청입니다.");
		}
	}

	/**
	 * 요청 파라미터에서 SQL 인젝션 패턴을 검사합니다.
	 *
	 * @param request {@link WrappedHttpServletRequest} 객체
	 * @throws ServletException SQL 인젝션이 발견된 경우 예외를 던집니다.
	 */
	private void validateRequestParameters(WrappedHttpServletRequest request) throws ServletException {
		request.getParameterMap().forEach((name, values) -> Arrays.stream(values).forEach(value -> {
			if (value != null && sqlInjectionProperties.getCompiledPattern().matcher(value).find()) {
				log.warn("파라미터에서 SQL 인젝션이 감지되었습니다: {}", name);
				try {
					throw new ServletException("파라미터에서 SQL 인젝션이 감지되었습니다: " + name);
				} catch (ServletException e) {
					throw new RuntimeException(e);
				}
			}
		}));
	}

	/**
	 * 요청 본문에서 SQL 인젝션 패턴을 검사합니다.
	 *
	 * @param request {@link WrappedHttpServletRequest} 객체
	 * @throws ServletException SQL 인젝션이 발견된 경우 예외를 던집니다.
	 */
	private void validateRequestBody(WrappedHttpServletRequest request) throws ServletException {
		String body = request.getBody();
		if (body != null && sqlInjectionProperties.getCompiledPattern().matcher(body).find()) {
			log.warn("요청 본문에서 SQL 인젝션이 감지되었습니다.");
			throw new ServletException("요청 본문에서 SQL 인젝션이 감지되었습니다.");
		}
	}

	/**
	 * 요청 쿠키에서 SQL 인젝션 패턴을 검사합니다.
	 *
	 * @param request {@link WrappedHttpServletRequest} 객체
	 * @throws ServletException SQL 인젝션이 발견된 경우 예외를 던집니다.
	 */
	private void validateCookies(WrappedHttpServletRequest request) throws ServletException {
		if (request.getCookies() != null) {
			Arrays.stream(request.getCookies()).forEach(cookie -> {
				if (cookie.getValue() != null && sqlInjectionProperties.getCompiledPattern()
					.matcher(cookie.getValue())
					.find()) {
					log.warn("쿠키에서 SQL 인젝션이 감지되었습니다: {}", cookie.getName());
					try {
						throw new ServletException("쿠키에서 SQL 인젝션이 감지되었습니다: " + cookie.getName());
					} catch (ServletException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}
}
