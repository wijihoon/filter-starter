package shinhancard.logging.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import shinhancard.common.wrapper.WrappedHttpServletRequest;
import shinhancard.common.wrapper.WrappedHttpServletResponse;
import shinhancard.logging.properties.LogProperties;
import shinhancard.logging.service.LogService;

/**
 * HTTP 요청 및 응답을 JSON 로그로 기록하고, 설정된 로그 전송 방식으로 전송하는 필터 클래스입니다.
 * <p>
 * 이 필터는 HTTP 요청과 응답을 감싸서 로그를 기록하고, 로그 서비스를 통해 전송합니다.
 * 요청 및 응답 본문은 설정에 따라 잘라낼 수 있습니다.
 * </p>
 */
@Slf4j
public class LogFilter extends OncePerRequestFilter {

	private static final String CONTEXT_HTTP_REQUEST = "REQUEST";
	private static final String CONTEXT_HTTP_RESPONSE = "RESPONSE";
	private static final String TRACE_ID_KEY = "traceId"; // 상수로 정의
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // JSON 변환기, 싱글턴으로 재사용
	private final LogProperties logProperties;
	private final LogService logService;

	/**
	 * LogFilter의 생성자입니다.
	 *
	 * @param logProperties 로그 설정을 담고 있는 {@link LogProperties} 객체
	 * @param logService 로그를 전송하는 {@link LogService} 객체
	 */
	public LogFilter(LogProperties logProperties, LogService logService) {
		this.logProperties = logProperties;
		this.logService = logService;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		// 정적 리소스나 특정 경로는 필터에서 제외
		String path = request.getRequestURI();
		return path.startsWith("/static/") || path.startsWith("/assets/") || path.endsWith(".css") || path.endsWith(
			".js");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);
		WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(response);
		String traceId = UUID.randomUUID().toString(); // 고유한 트레이스 ID 생성
		MDC.put(TRACE_ID_KEY, traceId);

		try {
			logRequest(wrappedRequest, traceId);
			filterChain.doFilter(wrappedRequest, wrappedResponse);
			wrappedResponse.flushBuffer();
			logResponse(wrappedResponse, traceId);
		} catch (Exception e) {
			log.error("요청 처리 중 오류 발생", e);
			throw e;
		} finally {
			MDC.clear();
		}
	}

	/**
	 * 요청 정보를 로그로 기록합니다.
	 * <p>
	 * 요청의 메소드, URI, 쿼리 문자열, 헤더, 본문 등을 로그로 기록합니다.
	 * 요청 본문은 설정에 따라 잘라내고 민감한 데이터는 마스킹 처리합니다.
	 * </p>
	 *
	 * @param request 요청을 감싼 {@link WrappedHttpServletRequest} 객체
	 * @param traceId 요청을 추적하기 위한 고유한 트레이스 ID
	 */
	private void logRequest(WrappedHttpServletRequest request, String traceId) {
		try {
			MDC.put("context", CONTEXT_HTTP_REQUEST);
			Map<String, Object> logData = buildRequestLogData(request, traceId);
			sendLogAsync(logData); // 비동기로 로그 전송
		} catch (Exception e) {
			log.error("요청 데이터 로그 기록 실패", e);
		}
	}

	/**
	 * 응답 정보를 로그로 기록합니다.
	 * <p>
	 * 응답의 상태 코드, 헤더, 본문 등을 로그로 기록합니다.
	 * 응답 본문은 설정에 따라 잘라내고 민감한 데이터는 마스킹 처리합니다.
	 * </p>
	 *
	 * @param response 응답을 감싼 {@link WrappedHttpServletResponse} 객체
	 * @param traceId 요청을 추적하기 위한 고유한 트레이스 ID
	 */
	private void logResponse(WrappedHttpServletResponse response, String traceId) {
		try {
			MDC.put("context", CONTEXT_HTTP_RESPONSE);
			Map<String, Object> logData = buildResponseLogData(response, traceId);
			sendLogAsync(logData); // 비동기로 로그 전송
		} catch (Exception e) {
			log.error("응답 데이터 로그 기록 실패", e);
		}
	}

	/**
	 * 요청 로그 데이터를 생성합니다.
	 *
	 * @param request 요청을 감싼 {@link WrappedHttpServletRequest} 객체
	 * @param traceId 요청을 추적하기 위한 고유한 트레이스 ID
	 * @return 요청 로그 데이터 맵
	 */
	private Map<String, Object> buildRequestLogData(WrappedHttpServletRequest request, String traceId) {
		Map<String, Object> logData = new HashMap<>();
		logData.put(TRACE_ID_KEY, traceId);
		logData.put("method", request.getMethod());
		logData.put("uri", request.getRequestURI());
		logData.put("query", request.getQueryString());
		logData.put("remoteAddress", request.getRemoteAddr());
		logData.put("headers", getHeadersMap(request));

		if (logProperties.getRequestBody().isTruncate()) {
			logData.put("body", truncateAndMaskBody(request.getBody()));
		}

		return logData;
	}

	/**
	 * 응답 로그 데이터를 생성합니다.
	 *
	 * @param response 응답을 감싼 {@link WrappedHttpServletResponse} 객체
	 * @param traceId 요청을 추적하기 위한 고유한 트레이스 ID
	 * @return 응답 로그 데이터 맵
	 */
	private Map<String, Object> buildResponseLogData(WrappedHttpServletResponse response, String traceId) {
		Map<String, Object> logData = new HashMap<>();
		logData.put(TRACE_ID_KEY, traceId);
		logData.put("status", response.getStatus());
		logData.put("headers", getHeadersMap(response));

		if (logProperties.getResponseBody().isTruncate()) {
			logData.put("body", truncateAndMaskBody(response.getBody()));
		}

		return logData;
	}

	/**
	 * 로그 데이터를 비동기로 전송합니다.
	 *
	 * @param logData 전송할 로그 데이터 맵
	 */
	private void sendLogAsync(Map<String, Object> logData) {
		CompletableFuture.runAsync(() -> {
			try {
				String logJson = OBJECT_MAPPER.writeValueAsString(logData);
				logService.sendLog(logJson);
				log.debug(logJson);
			} catch (Exception e) {
				log.warn("비동기 로그 전송 실패: {}", e.getMessage());
			}
		});
	}

	/**
	 * 요청 또는 응답의 헤더를 맵으로 변환합니다.
	 *
	 * @param requestOrResponse 요청 또는 응답 객체
	 * @return 헤더 이름과 값이 담긴 맵
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
	 * 요청 또는 응답 본문을 잘라내고 민감한 데이터를 마스킹합니다.
	 *
	 * @param body 요청 또는 응답 본문
	 * @return 잘라내고 마스킹 처리된 본문
	 */
	private String truncateAndMaskBody(String body) {
		if (body == null) {
			return "[No Content]";
		}
		String truncatedBody = body;
		int maxSize = logProperties.getBody().getMaxSize();
		if (body.length() > maxSize) {
			truncatedBody = body.substring(0, maxSize) + "... [TRUNCATED]";
		}
		return maskSensitiveData(truncatedBody);
	}

	/**
	 * 요청 또는 응답 본문에서 민감한 데이터를 마스킹합니다.
	 *
	 * @param body 요청 또는 응답 본문
	 * @return 민감한 데이터가 마스킹된 본문
	 */
	private String maskSensitiveData(String body) {
		for (String field : logProperties.getSensitiveFields()) {
			body = body.replaceAll(String.format("\"%s\":\"[^\"]*\"", field),
				String.format("\"%s\":\"[PROTECTED]\"", field));
		}
		return body;
	}
}
