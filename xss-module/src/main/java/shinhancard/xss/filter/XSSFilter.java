package shinhancard.xss.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import shinhancard.common.io.ErrorCode;
import shinhancard.common.io.ResponseVo;
import shinhancard.common.wrapper.WrappedHttpServletRequest;
import shinhancard.xss.properties.XSSProperties;

/**
 * XSS(교차 사이트 스크립팅) 공격을 탐지하고 방지하기 위한 필터입니다.
 * 이 필터는 HTTP 요청의 매개변수, 쿠키, 및 요청 본문에서 XSS 취약점을 검사합니다.
 */
@Slf4j
@Component
public class XSSFilter extends OncePerRequestFilter {

	private final XSSProperties xssProperties;

	/**
	 * XssFilter의 생성자입니다.
	 *
	 * @param xssProperties XSS 검사를 위한 패턴을 설정하는 XssProperties 객체
	 */
	@Autowired
	public XSSFilter(XSSProperties xssProperties) {
		this.xssProperties = xssProperties;
	}

	/**
	 * 요청을 필터링하여 XSS 공격을 검사하고, 감지된 경우 요청 처리를 중단합니다.
	 *
	 * @param request     HTTP 요청 객체
	 * @param response    HTTP 응답 객체
	 * @param filterChain 필터 체인
	 * @throws ServletException 서블릿 예외
	 * @throws IOException      I/O 예외
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);

		// 매개변수, 본문, 쿠키에서 XSS 공격 여부를 검사하고, 감지된 경우 필터 체인 진행을 중지합니다.
		if (isXssDetectedInParametersOrBody(wrappedRequest, response) || isXssDetectedInCookies(wrappedRequest,
			response)) {
			return; // XSS 감지 시 필터 체인 진행을 중지합니다.
		}

		filterChain.doFilter(wrappedRequest, response); // XSS가 감지되지 않은 경우 다음 필터로 요청을 전달합니다.
	}

	private boolean isXssDetectedInParametersOrBody(WrappedHttpServletRequest request,
		HttpServletResponse response) throws IOException {
		return isXssDetectedInRequestParameters(request, response) || checkForXssInRequestBody(request, response);
	}

	private boolean isXssDetectedInRequestParameters(HttpServletRequest request, HttpServletResponse response) throws
		IOException {
		Enumeration<String> parameterNames = request.getParameterNames();

		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			if (isEmpty(paramName)) {
				continue;
			}

			if (isXssDetectedInParameter(paramName, request, response)) {
				log.debug("XSS 감지됨: 파라미터 [{}]에 위험 값 존재", paramName);
				return true;
			}
		}

		return false;
	}

	private boolean isXssDetectedInParameter(String paramName, HttpServletRequest request,
		HttpServletResponse response) throws IOException {
		if (checkForXss(paramName, "parameter", response)) {
			return true;
		}

		String[] paramValues = request.getParameterValues(paramName);
		if (paramValues != null) {
			for (String paramValue : paramValues) {
				if (checkForXss(paramValue, "parameter", response)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isXssDetectedInCookies(HttpServletRequest request, HttpServletResponse response) throws
		IOException {
		Cookie[] cookies = request.getCookies();

		if (cookies == null || cookies.length == 0) {
			log.debug("요청에 쿠키가 없습니다.");
			return false;
		}

		for (Cookie cookie : cookies) {
			String cookieName = cookie.getName();
			String cookieValue = cookie.getValue();

			if (cookieName == null || cookieValue == null) {
				continue;
			}

			if (checkForXss(cookieName, "cookie", response) || checkForXss(cookieValue, "cookie", response)) {
				log.debug("XSS 감지됨: 쿠키 이름 [{}] 또는 값 [{}]", cookieName, cookieValue);
				return true;
			}
		}

		log.debug("XSS 감지되지 않음: 쿠키에 위험 값 없음");
		return false;
	}

	private boolean checkForXssInRequestBody(WrappedHttpServletRequest request, HttpServletResponse response) throws
		IOException {
		String body = request.getBody();
		return checkForXss(body, "body", response);
	}

	private boolean checkForXss(String value, String source, HttpServletResponse response) throws IOException {
		if (isEmpty(value)) {
			return false;
		}

		boolean isXssDetected = isXssDetected(value);

		if (isXssDetected) {
			logXssDetected(source, value);
			handleXssViolation(response, getErrorCodeForSource(source));
		}

		return isXssDetected;
	}

	private boolean isXssDetected(String value) {
		Pattern compiledPattern = xssProperties.getCompiledPattern();
		return compiledPattern.matcher(value).find();
	}

	private void logXssDetected(String source, String value) {
		log.warn("XSS 공격 가능성 발견됨: {}에서 발견됨 - {}", source, value);
	}

	private ErrorCode getErrorCodeForSource(String source) {
		return switch (source) {
			case "parameter" -> ErrorCode.XSS_IN_PARAMETER;
			case "cookie" -> ErrorCode.XSS_IN_COOKIE;
			case "body" -> ErrorCode.XSS_IN_BODY;
			default -> ErrorCode.XSS_DETECTED;
		};
	}

	private void handleXssViolation(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json");

		try (var writer = response.getWriter()) {
			writer.write(ResponseVo.error(errorCode.getMessage(), errorCode.getHttpStatus()).toString());
			writer.flush();
		}
	}

	private boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
}
