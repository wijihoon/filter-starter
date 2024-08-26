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
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // JSON 변환기, 싱글턴으로 재사용
	private final LogProperties logProperties;
	private final LogService logService;

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
		MDC.put("traceId", traceId);

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

	private void logRequest(WrappedHttpServletRequest request, String traceId) {
		try {
			MDC.put("context", CONTEXT_HTTP_REQUEST);
			Map<String, Object> logData = buildRequestLogData(request, traceId);
			sendLogAsync(logData); // 비동기로 로그 전송
		} catch (Exception e) {
			log.error("요청 데이터 로그 기록 실패", e);
		}
	}

	private void logResponse(WrappedHttpServletResponse response, String traceId) {
		try {
			MDC.put("context", CONTEXT_HTTP_RESPONSE);
			Map<String, Object> logData = buildResponseLogData(response, traceId);
			sendLogAsync(logData); // 비동기로 로그 전송
		} catch (Exception e) {
			log.error("응답 데이터 로그 기록 실패", e);
		}
	}

	private Map<String, Object> buildRequestLogData(WrappedHttpServletRequest request, String traceId) {
		Map<String, Object> logData = new HashMap<>();
		logData.put("traceId", traceId);
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

	private Map<String, Object> buildResponseLogData(WrappedHttpServletResponse response, String traceId) {
		Map<String, Object> logData = new HashMap<>();
		logData.put("traceId", traceId);
		logData.put("status", response.getStatus());
		logData.put("headers", getHeadersMap(response));

		if (logProperties.getResponseBody().isTruncate()) {
			logData.put("body", truncateAndMaskBody(response.getBody()));
		}

		return logData;
	}

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

	private String maskSensitiveData(String body) {
		for (String field : logProperties.getSensitiveFields()) {
			body = body.replaceAll(String.format("\"%s\":\"[^\"]*\"", field),
				String.format("\"%s\":\"[PROTECTED]\"", field));
		}
		return body;
	}
}
