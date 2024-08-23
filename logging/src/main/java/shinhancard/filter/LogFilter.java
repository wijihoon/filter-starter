package shinhancard.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import shinhancard.properties.LogProperties;
import shinhancard.service.LogService;
import shinhancard.wrapper.WrappedHttpServletRequest;
import shinhancard.wrapper.WrappedHttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HTTP 요청 및 응답을 JSON 로그로 기록하고, 설정된 로그 전송 방식으로 전송하는 필터 클래스입니다.
 * <p>
 * 이 필터는 HTTP 요청과 응답을 감싸서 로그를 기록하고, 로그 서비스를 통해 전송합니다.
 * 요청 및 응답 본문은 설정에 따라 잘라낼 수 있습니다.
 * </p>
 */
@Slf4j
@Component
public class LogFilter extends OncePerRequestFilter {

    private static final String CONTEXT_HTTP_REQUEST = "REQUEST";
    private static final String CONTEXT_HTTP_RESPONSE = "RESPONSE";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // JSON 변환기, 싱글턴으로 재사용
    private final LogProperties logProperties;
    private final LogService logService;

    /**
     * 생성자입니다. {@link LogProperties} 및 {@link LogService}를 주입받습니다.
     *
     * @param logProperties 로그 관련 설정을 담고 있는 {@link LogProperties} 객체
     * @param logService    로그를 전송하는 {@link LogService} 객체
     */
    public LogFilter(LogProperties logProperties, LogService logService) {
        this.logProperties = logProperties;
        this.logService = logService;
    }

    /**
     * HTTP 요청 및 응답을 감싸서 로그를 기록하고 필터 체인을 계속 진행합니다.
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException      IO 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);
        WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(response);

        try {
            logRequest(wrappedRequest);
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            wrappedResponse.flushBuffer();
            logResponse(wrappedResponse);
        } catch (Exception e) {
            log.error("요청 처리 중 오류 발생", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    /**
     * HTTP 요청의 로그를 기록합니다.
     *
     * @param request HTTP 요청을 감싼 {@link WrappedHttpServletRequest} 객체
     */
    private void logRequest(WrappedHttpServletRequest request) {
        try {
            MDC.put("context", CONTEXT_HTTP_REQUEST);
            Map<String, Object> logData = buildRequestLogData(request);
            sendLog(logData);
        } catch (Exception e) {
            log.error("요청 데이터 로그 기록 실패", e);
        }
    }

    /**
     * HTTP 응답의 로그를 기록합니다.
     *
     * @param response HTTP 응답을 감싼 {@link WrappedHttpServletResponse} 객체
     */
    private void logResponse(WrappedHttpServletResponse response) {
        try {
            MDC.put("context", CONTEXT_HTTP_RESPONSE);
            Map<String, Object> logData = buildResponseLogData(response);
            sendLog(logData);
        } catch (Exception e) {
            log.error("응답 데이터 로그 기록 실패", e);
        }
    }

    /**
     * HTTP 요청에 대한 로그 데이터를 생성합니다.
     *
     * @param request HTTP 요청을 감싼 {@link WrappedHttpServletRequest} 객체
     * @return 로그 데이터가 담긴 {@link Map}
     */
    private Map<String, Object> buildRequestLogData(WrappedHttpServletRequest request) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("method", request.getMethod());
        logData.put("uri", request.getRequestURI());
        logData.put("query", request.getQueryString());
        logData.put("remoteAddress", request.getRemoteAddr());
        logData.put("headers", getHeadersMap(request));

        if (logProperties.getRequestBody().isTruncate()) {
            logData.put("body", truncateBody(request.getBody()));
        }

        return logData;
    }

    /**
     * HTTP 응답에 대한 로그 데이터를 생성합니다.
     *
     * @param response HTTP 응답을 감싼 {@link WrappedHttpServletResponse} 객체
     * @return 로그 데이터가 담긴 {@link Map}
     */
    private Map<String, Object> buildResponseLogData(WrappedHttpServletResponse response) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("status", response.getStatus());
        logData.put("headers", getHeadersMap(response));

        if (logProperties.getResponseBody().isTruncate()) {
            logData.put("body", truncateBody(response.getBody()));
        }

        return logData;
    }

    /**
     * 로그 데이터를 JSON 형식으로 변환하여 로그 서비스를 통해 전송합니다.
     *
     * @param logData 로그 데이터가 담긴 {@link Map}
     * @throws IOException JSON 변환 중 예외 발생 시
     */
    private void sendLog(Map<String, Object> logData) throws IOException {
        String logJson = OBJECT_MAPPER.writeValueAsString(logData);
        logService.sendLog(logJson);
        log.debug(logJson);
    }

    /**
     * 요청 또는 응답의 헤더를 {@link Map}으로 변환합니다.
     *
     * @param requestOrResponse 요청 또는 응답 객체
     * @return 헤더가 담긴 {@link Map}
     */
    private Map<String, String> getHeadersMap(Object requestOrResponse) {
        if (requestOrResponse instanceof WrappedHttpServletRequest wrappedRequest) {
            return Collections.list(wrappedRequest.getHeaderNames()).stream()
                    .collect(Collectors.toMap(
                            headerName -> headerName,
                            wrappedRequest::getHeader,
                            (existing, replacement) -> existing + ", " + replacement
                    ));
        } else if (requestOrResponse instanceof WrappedHttpServletResponse wrappedResponse) {
            return wrappedResponse.getHeaderNames().stream()
                    .collect(Collectors.toMap(
                            headerName -> headerName,
                            wrappedResponse::getHeader,
                            (existing, replacement) -> existing + ", " + replacement
                    ));
        }
        return new HashMap<>();
    }

    /**
     * 로그 본문을 잘라냅니다. 본문이 너무 길면 최대 크기로 잘라내고 "[TRUNCATED]"를 추가합니다.
     *
     * @param body 본문 문자열
     * @return 잘라낸 본문 문자열
     */
    private String truncateBody(String body) {
        if (body == null) {
            return "[No Content]";
        }
        int maxSize = logProperties.getBody().getMaxSize();
        if (body.length() > maxSize) {
            return body.substring(0, maxSize) + "... [TRUNCATED]";
        }
        return body;
    }
}
