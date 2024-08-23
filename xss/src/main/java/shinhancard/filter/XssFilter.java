package shinhancard.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import shinhancard.io.ErrorCode;
import shinhancard.io.ResponseVo;
import shinhancard.properties.XssProperties;
import shinhancard.wrapper.WrappedHttpServletRequest;

/**
 * XSS(교차 사이트 스크립팅) 공격을 탐지하고 방지하기 위한 필터입니다.
 * 이 필터는 HTTP 요청의 매개변수, 쿠키 및 요청 본문에서 XSS 취약점을 검사합니다.
 */
@Slf4j
@Component
public class XssFilter extends OncePerRequestFilter {

	private final XssProperties xssProperties;

	@Autowired
	public XssFilter(XssProperties xssProperties) {
		this.xssProperties = xssProperties;
	}

	/**
	 * 요청에 대해 XSS를 검사하고, XSS가 탐지되면 필터 체인 진행을 중지합니다.
	 *
	 * @param request HTTP 요청 객체입니다.
	 * @param response HTTP 응답 객체입니다.
	 * @param filterChain 필터 체인 객체입니다.
	 * @throws ServletException 필터 처리 중 오류가 발생한 경우입니다.
	 * @throws IOException I/O 오류가 발생한 경우입니다.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		// 요청 본문을 캐시하고 매개변수를 검사하기 위해 요청을 래핑합니다.
		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);

		// 매개변수, 쿠키 및 본문에서 XSS를 검사합니다.
		if (checkForXssInParameters(wrappedRequest, response) ||
			checkForXssInRequestBody(wrappedRequest, response)) {
			return; // XSS 감지 시 필터 체인 진행을 중지합니다.
		}

		// 필터 체인 계속 진행
		filterChain.doFilter(wrappedRequest, response);
	}

	/**
	 * HTTP 요청의 매개변수와 쿠키에서 XSS를 검사합니다.
	 *
	 * @param request HTTP 요청 객체입니다.
	 * @param response HTTP 응답 객체입니다.
	 * @return XSS가 탐지되면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
	 * @throws IOException I/O 오류가 발생한 경우입니다.
	 */
	private boolean checkForXssInParameters(HttpServletRequest request, HttpServletResponse response) throws
		IOException {
		boolean xssDetected = false;

		// 매개변수 검사
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			if (checkForXss(paramName, "parameter", response)) {
				xssDetected = true;
				break;
			}
			String[] paramValues = request.getParameterValues(paramName);
			for (String paramValue : paramValues) {
				if (checkForXss(paramValue, "parameter", response)) {
					xssDetected = true;
					break;
				}
			}
			if (xssDetected)
				break;
		}

		// 쿠키 검사
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (checkForXss(cookie.getName(), "cookie", response) ||
					checkForXss(cookie.getValue(), "cookie", response)) {
					xssDetected = true;
					break;
				}
			}
		}

		return xssDetected;
	}

	/**
	 * 요청 본문에서 XSS를 검사합니다.
	 *
	 * @param request 요청 본문을 래핑한 WrappedHttpServletRequest 객체입니다.
	 * @param response HTTP 응답 객체입니다.
	 * @return XSS가 탐지되면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
	 * @throws IOException I/O 오류가 발생한 경우입니다.
	 */
	private boolean checkForXssInRequestBody(WrappedHttpServletRequest request, HttpServletResponse response) throws
		IOException {
		String body = request.getBody();
		return checkForXss(body, "body", response);
	}

	/**
	 * 주어진 값에서 XSS를 검사합니다.
	 *
	 * @param value 검사할 값입니다.
	 * @param source 값이 원래 포함된 소스(예: 매개변수, 쿠키, 본문)입니다.
	 * @param response HTTP 응답 객체입니다.
	 * @return XSS가 탐지되면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
	 * @throws IOException I/O 오류가 발생한 경우입니다.
	 */
	private boolean checkForXss(String value, String source, HttpServletResponse response) throws IOException {
		if (value != null && xssProperties.getPatterns().matcher(value).find()) {
			String errorMessage = String.format("XSS 공격 가능성 발견됨: %s에서 발견됨 - %s", source, value);
			log.warn(errorMessage);

			// 각 소스별로 에러 코드 지정
			ErrorCode errorCode = getErrorCodeForSource(source);
			handleXssViolation(response, errorCode);
			return true;
		}
		return false;
	}

	/**
	 * XSS가 발견된 소스에 따라 적절한 에러 코드를 반환합니다.
	 *
	 * @param source XSS가 발견된 소스(예: 매개변수, 쿠키, 본문)입니다.
	 * @return 해당 소스에 대한 에러 코드입니다.
	 */
	private ErrorCode getErrorCodeForSource(String source) {
		return switch (source) {
			case "parameter" -> ErrorCode.XSS_IN_PARAMETER;
			case "cookie" -> ErrorCode.XSS_IN_COOKIE;
			case "body" -> ErrorCode.XSS_IN_BODY;
			default -> ErrorCode.XSS_DETECTED; // 기본 XSS 에러 코드
		};
	}

	/**
	 * XSS 위반이 발견되었을 때 응답을 처리합니다.
	 *
	 * @param response HTTP 응답 객체입니다.
	 * @param errorCode 발생한 에러의 코드입니다.
	 * @throws IOException I/O 오류가 발생한 경우입니다.
	 */
	private void handleXssViolation(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(HttpStatus.FORBIDDEN.value()); // 403 Forbidden 상태 설정
		response.setContentType("application/json"); // 응답의 Content-Type을 JSON으로 설정
		try (var writer = response.getWriter()) {
			writer.write(ResponseVo.error(errorCode.getMessage(), errorCode.getHttpStatus()).toString()); // 에러 메시지를 JSON 형식으로 작성
			writer.flush(); // 응답을 강제로 플러시하여 전송
		}
	}
}
