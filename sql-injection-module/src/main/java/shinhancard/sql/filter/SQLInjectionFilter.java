package shinhancard.sql.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shinhancard.common.io.ResponseCode;
import shinhancard.common.io.ResponseVo;
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

		// SQL 인젝션 검사 수행
		boolean isParameterSafe = validateRequestParameters(wrappedRequest);
		boolean isBodySafe = validateRequestBody(wrappedRequest);
		boolean isCookiesSafe = validateCookies(wrappedRequest);

		if (!isParameterSafe) {
			handleSqlInjectionViolation(response, ResponseCode.SQL_INJECTION_PARAMETER_DETECTED);
			return;
		}

		if (!isBodySafe) {
			handleSqlInjectionViolation(response, ResponseCode.SQL_INJECTION_BODY_DETECTED);
			return;
		}

		if (!isCookiesSafe) {
			handleSqlInjectionViolation(response, ResponseCode.SQL_INJECTION_COOKIE_DETECTED);
			return;
		}

		// 필터 체인을 계속 진행합니다.
		filterChain.doFilter(wrappedRequest, response);
	}

	/**
	 * 요청 파라미터에서 SQL 인젝션 패턴을 검사합니다.
	 *
	 * @param request {@link WrappedHttpServletRequest} 객체
	 * @return 파라미터가 안전한 경우 true, 그렇지 않으면 false
	 */
	private boolean validateRequestParameters(WrappedHttpServletRequest request) {
		return request.getParameterMap().entrySet().stream()
			.flatMap(entry -> Arrays.stream(entry.getValue()))
			.noneMatch(value -> value != null && sqlInjectionProperties.getCompiledPattern().matcher(value).find());
	}

	/**
	 * 요청 본문에서 SQL 인젝션 패턴을 검사합니다.
	 *
	 * @param request {@link WrappedHttpServletRequest} 객체
	 * @return 본문이 안전한 경우 true, 그렇지 않으면 false
	 */
	private boolean validateRequestBody(WrappedHttpServletRequest request) {
		String body = request.getBody();
		return body == null || !sqlInjectionProperties.getCompiledPattern().matcher(body).find();
	}

	/**
	 * 요청 쿠키에서 SQL 인젝션 패턴을 검사합니다.
	 *
	 * @param request {@link WrappedHttpServletRequest} 객체
	 * @return 쿠키가 안전한 경우 true, 그렇지 않으면 false
	 */
	private boolean validateCookies(WrappedHttpServletRequest request) {
		if (request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
				.noneMatch(cookie -> cookie.getValue() != null && sqlInjectionProperties.getCompiledPattern()
					.matcher(cookie.getValue())
					.find());
		}
		return true;
	}

	/**
	 * SQL 인젝션 감지 시 에러 응답을 처리합니다.
	 *
	 * @param response  {@link HttpServletResponse} 객체
	 * @param responseCode {@link ResponseCode} 에러 코드
	 * @throws IOException 입출력 예외
	 */
	private void handleSqlInjectionViolation(HttpServletResponse response, ResponseCode responseCode) throws
		IOException {
		log.error("SQL 인젝션 예외 발생: {}", responseCode.getMessage());

		response.sendError(
			responseCode.getHttpStatus().value(),
			ResponseVo.error(responseCode, Optional.empty()).toString()
		);

		log.debug("SQL 인젝션 정책 위반: {} 응답을 전송했습니다.", responseCode);
	}
}
