package shinhancard.common.io;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import shinhancard.common.exception.JsonProcessingException;

/**
 * 응답 데이터를 표현하는 불변 객체입니다.
 *
 * @param <T> 응답 데이터의 타입
 */
public record ResponseVo<T>(
	DataHeader dataHeader,
	T dataBody
) {
	private static final ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper()
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	/**
	 * 성공 응답을 생성합니다.
	 *
	 * @param data 응답 데이터. 성공적인 요청에 대한 데이터를 포함합니다.
	 * @param responseCode 응답 코드 enum (ResponseCode 사용)
	 * @param traceId 요청에서 전달된 traceId 또는 새로 생성된 traceId
	 * @param <T> 응답 데이터의 타입
	 * @return 성공 응답을 담은 ResponseVo 객체
	 */
	public static <T> ResponseVo<T> success(T data, ResponseCode responseCode, Optional<String> traceId) {
		DataHeader header = new DataHeader(
			responseCode.getCode(),
			responseCode.getMessage(),
			traceId.orElseGet(ResponseVo::generateTraceId)
		);
		return new ResponseVo<>(header, data);
	}

	/**
	 * 에러 응답을 생성합니다.
	 *
	 * @param responseCode 응답 코드 enum (ResponseCode 사용)
	 * @param traceId 요청에서 전달된 traceId 또는 새로 생성된 traceId
	 * @param <T> 응답 데이터의 타입. 이 메서드에서는 데이터가 null로 설정됩니다.
	 * @return 실패 응답을 담은 ResponseVo 객체
	 */
	public static <T> ResponseVo<T> error(ResponseCode responseCode, Optional<String> traceId) {
		DataHeader header = new DataHeader(
			responseCode.getCode(),
			responseCode.getMessage(),
			traceId.orElseGet(ResponseVo::generateTraceId)
		);
		return new ResponseVo<>(header, null);
	}

	/**
	 * traceId를 생성합니다. 예시로 간단히 UUID 기반으로 생성합니다.
	 *
	 * @return 생성된 traceId
	 */
	private static String generateTraceId() {
		return java.util.UUID.randomUUID().toString();
	}

	/**
	 * JSON 형식의 문자열을 반환합니다.
	 *
	 * @return JSON 형식의 문자열
	 */
	@Override
	public String toString() {
		try {
			return objectMapper.writeValueAsString(this);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			throw new JsonProcessingException(ResponseCode.JSON_PROCESSING_ERROR);
		}
	}

	/**
	 * 응답의 헤더 정보를 담는 레코드입니다.
	 */
	public record DataHeader(
		String resultCode,
		String resultMessage,
		String traceId
	) {
	}
}
