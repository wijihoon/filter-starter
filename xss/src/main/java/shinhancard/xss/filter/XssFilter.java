package shinhancard.xss.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import shinhancard.common.io.ErrorCode;
import shinhancard.common.io.ResponseVo;
import shinhancard.xss.properties.XssProperties;
import shinhancard.common.wrapper.WrappedHttpServletRequest;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * XSS(교차 사이트 스크립팅) 공격을 탐지하고 방지하기 위한 필터입니다.
 * 이 필터는 HTTP 요청의 매개변수, 쿠키, 및 요청 본문에서 XSS 취약점을 검사합니다.
 */
@Slf4j
@Component
public class XssFilter extends OncePerRequestFilter {

    private final XssProperties xssProperties;

    /**
     * XssFilter의 생성자입니다.
     *
     * @param xssProperties XSS 검사를 위한 패턴을 설정하는 XssProperties 객체
     */
    @Autowired
    public XssFilter(XssProperties xssProperties) {
        this.xssProperties = xssProperties; // XssProperties 객체를 주입받아 필터에 저장합니다.
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
        WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request); // 요청을 래핑하여 XSS 검사를 수행합니다.

        // 매개변수, 본문, 쿠키에서 XSS 공격 여부를 검사하고, 감지된 경우 필터 체인 진행을 중지합니다.
        if (isXssDetectedInParametersOrBody(wrappedRequest, response) || isXssDetectedInCookies(wrappedRequest, response)) {
            return; // XSS 감지 시 필터 체인 진행을 중지합니다.
        }

        filterChain.doFilter(wrappedRequest, response); // XSS가 감지되지 않은 경우 다음 필터로 요청을 전달합니다.
    }

    /**
     * 요청의 매개변수와 본문에서 XSS 공격을 검사합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return XSS가 감지되면 true, 그렇지 않으면 false
     * @throws IOException I/O 예외
     */
    private boolean isXssDetectedInParametersOrBody(WrappedHttpServletRequest request, HttpServletResponse response) throws IOException {
        // 요청의 매개변수나 본문에서 XSS 공격을 검사합니다.
        return isXssDetectedInRequestParameters(request, response) || checkForXssInRequestBody(request, response);
    }

    /**
     * 요청의 매개변수에서 XSS 공격을 검사합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return XSS가 감지되면 true, 그렇지 않으면 false
     * @throws IOException I/O 예외
     */
    private boolean isXssDetectedInRequestParameters(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Enumeration<String> parameterNames = request.getParameterNames(); // 요청의 매개변수 이름을 가져옵니다.

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (isEmpty(paramName)) {
                continue; // 매개변수 이름이 null이거나 비어있으면 무시합니다.
            }

            if (isXssDetectedInParameter(paramName, request, response)) {
                log.debug("XSS 감지됨: 파라미터 [{}]에 위험 값 존재", paramName);
                return true; // XSS가 감지된 경우 즉시 true를 반환합니다.
            }
        }

        return false; // 모든 매개변수를 검사한 후 XSS가 감지되지 않으면 false를 반환합니다.
    }

    /**
     * 특정 매개변수에서 XSS 공격을 검사합니다.
     *
     * @param paramName 매개변수 이름
     * @param request   HTTP 요청 객체
     * @param response  HTTP 응답 객체
     * @return XSS가 감지되면 true, 그렇지 않으면 false
     * @throws IOException I/O 예외
     */
    private boolean isXssDetectedInParameter(String paramName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (checkForXss(paramName, "parameter", response)) {
            return true; // 매개변수 이름에서 XSS가 감지된 경우 true를 반환합니다.
        }

        String[] paramValues = request.getParameterValues(paramName); // 매개변수 이름에 대한 값을 가져옵니다.
        if (paramValues != null) {
            for (String paramValue : paramValues) {
                if (checkForXss(paramValue, "parameter", response)) {
                    return true; // 매개변수 값에서 XSS가 감지된 경우 true를 반환합니다.
                }
            }
        }
        return false; // 모든 매개변수를 검사한 후 XSS가 감지되지 않으면 false를 반환합니다.
    }

    /**
     * 요청의 쿠키에서 XSS 공격을 검사합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return XSS가 감지되면 true, 그렇지 않으면 false
     * @throws IOException I/O 예외
     */
    private boolean isXssDetectedInCookies(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies(); // 요청의 쿠키를 가져옵니다.

        if (cookies == null || cookies.length == 0) {
            log.debug("요청에 쿠키가 없습니다.");
            return false; // 쿠키가 없으면 XSS 감지 여부를 검사할 필요가 없습니다.
        }

        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();

            if (cookieName == null || cookieValue == null) {
                continue; // 쿠키 이름이나 값이 null이면 무시합니다.
            }

            if (checkForXss(cookieName, "cookie", response) || checkForXss(cookieValue, "cookie", response)) {
                log.debug("XSS 감지됨: 쿠키 이름 [{}] 또는 값 [{}]", cookieName, cookieValue);
                return true; // 쿠키 이름이나 값에서 XSS가 감지된 경우 true를 반환합니다.
            }
        }

        log.debug("XSS 감지되지 않음: 쿠키에 위험 값 없음");
        return false; // 모든 쿠키를 검사한 후 XSS가 감지되지 않으면 false를 반환합니다.
    }

    /**
     * 요청 본문에서 XSS 공격을 검사합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return XSS가 감지되면 true, 그렇지 않으면 false
     * @throws IOException I/O 예외
     */
    private boolean checkForXssInRequestBody(WrappedHttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = request.getBody(); // 요청 본문을 가져옵니다.
        return checkForXss(body, "body", response); // 요청 본문에서 XSS를 검사합니다.
    }

    /**
     * 값에서 XSS 공격을 검사하고, 감지된 경우 적절한 응답을 처리합니다.
     *
     * @param value    검사할 값
     * @param source   XSS가 감지된 소스 (parameter, cookie, body)
     * @param response HTTP 응답 객체
     * @return XSS가 감지되면 true, 그렇지 않으면 false
     * @throws IOException I/O 예외
     */
    private boolean checkForXss(String value, String source, HttpServletResponse response) throws IOException {
        if (isEmpty(value)) {
            return false; // 값이 null이거나 비어있으면 XSS 검사 불필요합니다.
        }

        if (isXssDetected(value)) {
            logXssDetected(source, value); // XSS가 감지된 경우 로그를 남깁니다.
            handleXssViolation(response, getErrorCodeForSource(source)); // XSS 위반을 처리합니다.
            return true;
        }

        return false; // XSS가 감지되지 않으면 false를 반환합니다.
    }

    /**
     * 값에서 XSS 공격 가능성을 검사합니다.
     *
     * @param value 검사할 값
     * @return XSS가 감지되면 true, 그렇지 않으면 false
     */
    private boolean isXssDetected(String value) {
        Pattern compiledPattern = xssProperties.getCompiledPattern(); // XSS 검사에 사용할 패턴을 가져옵니다.
        return compiledPattern.matcher(value).find(); // 값에서 XSS 공격 가능성을 검사합니다.
    }

    /**
     * XSS 감지 로그를 남깁니다.
     *
     * @param source XSS가 감지된 소스 (parameter, cookie, body)
     * @param value  감지된 XSS 값
     */
    private void logXssDetected(String source, String value) {
        log.warn("XSS 공격 가능성 발견됨: {}에서 발견됨 - {}", source, value); // XSS 감지 로그를 남깁니다.
    }

    /**
     * XSS 감지된 소스에 따라 적절한 에러 코드를 반환합니다.
     *
     * @param source XSS가 감지된 소스 (parameter, cookie, body)
     * @return 에러 코드
     */
    private ErrorCode getErrorCodeForSource(String source) {
        // XSS 감지된 소스에 따라 적절한 에러 코드를 반환합니다.
        return switch (source) {
            case "parameter" -> ErrorCode.XSS_IN_PARAMETER;
            case "cookie" -> ErrorCode.XSS_IN_COOKIE;
            case "body" -> ErrorCode.XSS_IN_BODY;
            default -> ErrorCode.XSS_DETECTED;
        };
    }

    /**
     * XSS 위반을 처리하고, 클라이언트에 적절한 오류 응답을 전송합니다.
     *
     * @param response  HTTP 응답 객체
     * @param errorCode 에러 코드
     * @throws IOException I/O 예외
     */
    private void handleXssViolation(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value()); // HTTP 응답 상태를 403 Forbidden으로 설정합니다.
        response.setContentType("application/json"); // 응답 콘텐츠 유형을 JSON으로 설정합니다.

        try (var writer = response.getWriter()) {
            writer.write(ResponseVo.error(errorCode.getMessage(), errorCode.getHttpStatus()).toString()); // XSS 위반 응답을 작성합니다.
            writer.flush(); // 응답을 플러시합니다.
        }
    }

    /**
     * 값이 null이거나 비어있는지 확인합니다.
     *
     * @param value 확인할 값
     * @return 값이 null이거나 비어있으면 true, 그렇지 않으면 false
     */
    private boolean isEmpty(String value) {
        return value == null || value.isEmpty(); // 값이 null이거나 비어있는지 확인합니다.
    }
}
