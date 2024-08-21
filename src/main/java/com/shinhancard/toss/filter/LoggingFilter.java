package com.shinhancard.toss.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhancard.toss.properties.LoggingProperties;
import com.shinhancard.toss.service.LogService;
import com.shinhancard.toss.wrapper.WrappedHttpServletRequest;
import com.shinhancard.toss.wrapper.WrappedHttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP 요청과 응답을 JSON 로그로 기록하고 Kafka로 전송하는 필터 클래스입니다.
 * <p>
 * 이 필터는 요청 및 응답을 래핑하여 본문을 읽고, JSON 형태로 Kafka로 로그를 전송합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

	private static final String CONTEXT_HTTP_REQUEST = "REQUEST";
	private static final String CONTEXT_HTTP_RESPONSE = "RESPONSE";

	private final LoggingProperties loggingProperties; // 설정 프로퍼티 주입
	private final LogService logService; // 로그 전송 서비스
	private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기

	/**
	 * 요청 및 응답을 필터링하고 로그를 기록합니다.
	 * <p>
	 * 요청 및 응답을 래핑하여 본문을 읽고 로그를 기록하며, 필터 체인을 계속합니다.
	 * </p>
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
			logRequest(wrappedRequest); // 요청 로그 기록
			filterChain.doFilter(wrappedRequest, wrappedResponse); // 필터 체인 계속
			logResponse(wrappedResponse); // 응답 로그 기록
		} catch (Exception e) {
			log.error("Error occurred during request processing", e); // 예외 로그 기록
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
	void logRequest(WrappedHttpServletRequest request) throws IOException {
		MDC.put("context", CONTEXT_HTTP_REQUEST); // MDC에 컨텍스트 설정

		Map<String, Object> logData = buildRequestLogData(request); // 요청 로그 데이터 생성

		sendLog(logData); // 로그 전송
	}

	/**
	 * HTTP 응답 정보를 로그로 기록하고 전송합니다.
	 *
	 * @param response HTTP 응답을 래핑한 {@link WrappedHttpServletResponse} 객체
	 * @throws IOException 응답 본문을 읽을 때 발생할 수 있는 예외
	 */
	void logResponse(WrappedHttpServletResponse response) throws IOException {
		MDC.put("context", CONTEXT_HTTP_RESPONSE); // MDC에 컨텍스트 설정

		Map<String, Object> logData = buildResponseLogData(response); // 응답 로그 데이터 생성

		sendLog(logData); // 로그 전송
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

		if (loggingProperties.getRequestBody().isTruncate()) {
			logData.put("body", truncateBody(request.getBody())); // 본문 자르기
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
		logData.put("headers", getHeadersMap((HttpServletRequest)response)); // 헤더 정보

		if (loggingProperties.getResponseBody().isTruncate()) {
			logData.put("body", truncateBody(response.getBody())); // 본문 자르기
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
		String logJson = objectMapper.writeValueAsString(logData); // 로그 데이터 JSON 변환
		logService.sendLog(logJson); // 로그 전송
		log.debug(logJson); // 디버깅을 위해 로그를 콘솔에도 출력
	}

	/**
	 * 요청 또는 응답의 헤더 정보를 맵으로 반환합니다.
	 *
	 * @param requestOrResponse {@link HttpServletRequest} 또는 {@link HttpServletResponse}
	 * @return 헤더 정보를 담은 맵
	 */
	private Map<String, String> getHeadersMap(HttpServletRequest requestOrResponse) {
		Map<String, String> headers = new HashMap<>();
		Enumeration<String> headerNames = requestOrResponse.getHeaderNames(); // 헤더 이름 열거형
		// Enumeration을 Stream으로 변환하여 처리
		Stream.of(headerNames).flatMap(headerName -> {
			String headerValue = requestOrResponse.getHeader(String.valueOf(headerName));
			return Stream.of(Map.entry(headerName, headerValue));
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // 헤더를 맵으로 변환
		return headers;
	}

	/**
	 * 요청 또는 응답 본문이 너무 길 경우 일부만 로그에 기록하도록 본문을 자릅니다.
	 *
	 * @param body 요청 또는 응답 본문
	 * @return 잘린 본문 문자열
	 */
	String truncateBody(String body) {
		int maxSize = loggingProperties.getBody().getMaxSize(); // 최대 크기
		if (body.length() > maxSize) {
			return body.substring(0, maxSize) + "... [TRUNCATED]"; // 본문 자르기
		}
		return body; // 본문이 최대 크기 이내일 경우 원본 반환
	}
}
