package shinhancard.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import shinhancard.properties.LoggingProperties;
import shinhancard.service.LogService;
import shinhancard.wrapper.WrappedHttpServletRequest;
import shinhancard.wrapper.WrappedHttpServletResponse;

/**
 * HTTP 요청과 응답을 JSON 로그로 기록하고 Kafka로 전송하는 필터 클래스입니다.
 * <p>
 * 이 필터는 요청 및 응답을 래핑하여 본문을 읽고, JSON 형태로 Kafka로 로그를 전송합니다.
 * </p>
 */
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

	// MDC 컨텍스트 구분자 (요청, 응답)
	private static final String CONTEXT_HTTP_REQUEST = "REQUEST";
	private static final String CONTEXT_HTTP_RESPONSE = "RESPONSE";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // JSON 변환기, 싱글턴으로 재사용
	private final LoggingProperties loggingProperties;
	private final LogService logService;

	public LoggingFilter(LoggingProperties loggingProperties, LogService logService) {
		this.loggingProperties = loggingProperties;
		this.logService = logService;
	}

	/**
	 * 요청 및 응답을 필터링하고 로그를 기록합니다.
	 *
	 * @param request     필터 체인에 전달된 {@link HttpServletRequest}
	 * @param response    필터 체인에 전달된 {@link HttpServletResponse}
	 * @param filterChain 필터 체인
	 * @throws ServletException 필터 처리 중 발생할 수 있는 예외
	 * @throws IOException      입출력 처리 중 발생할 수 있는 예외
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);
		WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(response);

		try {
			// 요청 로그 기록
			logRequest(wrappedRequest);
			// 필터 체인 계속
			filterChain.doFilter(wrappedRequest, wrappedResponse);
			// 응답 로그 기록
			wrappedResponse.flushBuffer(); // 응답 본문을 기록하기 위해 flushBuffer 호출
			logResponse(wrappedResponse);
		} catch (Exception e) {
			// 예외 발생 시 로그 기록
			log.error("요청 처리 중 오류 발생", e);
			throw e; // 예외를 다시 던져서 처리하도록 함
		} finally {
			MDC.clear(); // 메모리 누수 방지를 위해 MDC 초기화
		}
	}

	/**
	 * HTTP 요청 정보를 로그로 기록하고 전송합니다.
	 *
	 * @param request HTTP 요청을 래핑한 {@link WrappedHttpServletRequest} 객체
	 * @throws IOException 요청 본문을 읽을 때 발생할 수 있는 예외
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
	 * HTTP 응답 정보를 로그로 기록하고 전송합니다.
	 *
	 * @param response HTTP 응답을 래핑한 {@link WrappedHttpServletResponse} 객체
	 * @throws IOException 응답 본문을 읽을 때 발생할 수 있는 예외
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
	 * 요청 로그 데이터를 생성합니다.
	 *
	 * @param request {@link WrappedHttpServletRequest}
	 * @return 요청 로그 데이터 맵
	 */
	private Map<String, Object> buildRequestLogData(WrappedHttpServletRequest request) {
		Map<String, Object> logData = new HashMap<>();
		logData.put("method", request.getMethod()); // HTTP 메서드
		logData.put("uri", request.getRequestURI()); // 요청 URI
		logData.put("query", request.getQueryString()); // 쿼리 문자열
		logData.put("remoteAddress", request.getRemoteAddr()); // 원격 주소
		logData.put("headers", getHeadersMap(request)); // 헤더 정보

		// 본문 로그가 너무 길 경우 잘라서 기록
		if (loggingProperties.getRequestBody().isTruncate()) {
			logData.put("body", truncateBody(request.getBody()));
		}

		return logData;
	}

	/**
	 * 응답 로그 데이터를 생성합니다.
	 *
	 * @param response {@link WrappedHttpServletResponse}
	 * @return 응답 로그 데이터 맵
	 */
	private Map<String, Object> buildResponseLogData(WrappedHttpServletResponse response) {
		Map<String, Object> logData = new HashMap<>();
		logData.put("status", response.getStatus()); // 응답 상태 코드
		logData.put("headers", getHeadersMap(response)); // 헤더 정보

		// 본문 로그가 너무 길 경우 잘라서 기록
		if (loggingProperties.getResponseBody().isTruncate()) {
			logData.put("body", truncateBody(response.getBody()));
		}

		return logData;
	}

	/**
	 * 로그 데이터를 JSON 형식으로 변환하고 전송합니다.
	 *
	 * @param logData 전송할 로그 데이터
	 * @throws IOException JSON 변환 중 발생할 수 있는 예외
	 */
	private void sendLog(Map<String, Object> logData) throws IOException {
		String logJson = OBJECT_MAPPER.writeValueAsString(logData);
		logService.sendLog(logJson);
		log.debug(logJson);
	}

	/**
	 * 요청 또는 응답의 헤더 정보를 맵으로 반환합니다.
	 *
	 * @param requestOrResponse {@link HttpServletRequest} 또는 {@link HttpServletResponse}
	 * @return 헤더 정보를 담은 맵
	 */
	private Map<String, String> getHeadersMap(Object requestOrResponse) {
		if (requestOrResponse instanceof WrappedHttpServletRequest wrappedRequest) {
			return Collections.list(wrappedRequest.getHeaderNames()).stream()
				.collect(Collectors.toMap(
					headerName -> headerName,
					wrappedRequest::getHeader,
					(existing, replacement) -> existing + ", " + replacement // 중복된 값을 쉼표로 구분하여 병합
				));
		} else if (requestOrResponse instanceof WrappedHttpServletResponse wrappedResponse) {
			return wrappedResponse.getHeaderNames().stream()
				.collect(Collectors.toMap(
					headerName -> headerName,
					wrappedResponse::getHeader,
					(existing, replacement) -> existing + ", " + replacement // 중복된 값을 쉼표로 구분하여 병합
				));
		}
		return new HashMap<>();
	}

	/**
	 * 요청 또는 응답 본문이 너무 길 경우 일부만 로그에 기록하도록 본문을 자릅니다.
	 *
	 * @param body 요청 또는 응답 본문
	 * @return 잘린 본문 문자열
	 */
	private String truncateBody(String body) {
		if (body == null) {
			return "[No Content]";
		}
		int maxSize = loggingProperties.getBody().getMaxSize();
		if (body.length() > maxSize) {
			return body.substring(0, maxSize) + "... [TRUNCATED]";
		}
		return body;
	}
}
