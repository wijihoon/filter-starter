package shinhancard.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import shinhancard.common.io.ResponseCode;

/**
 * JSON 처리 중 발생하는 예외를 나타내는 커스텀 예외 클래스입니다.
 */
@Getter
public class JsonProcessingException extends RuntimeException {

	private final ResponseCode responseCode;

	/**
	 * JSON 처리 예외를 생성합니다.
	 *
	 * @param responseCode 응답 코드
	 */
	public JsonProcessingException(ResponseCode responseCode) {
		super(responseCode.getMessage());
		this.responseCode = responseCode;
	}

	/**
	 * HTTP 상태 코드를 반환합니다.
	 *
	 * @return HTTP 상태 코드
	 */
	public HttpStatus getHttpStatus() {
		return responseCode.getHttpStatus();
	}

	/**
	 * 오류 코드를 반환합니다.
	 *
	 * @return 오류 코드
	 */
	public String getErrorCode() {
		return responseCode.getCode();
	}

	/**
	 * 오류 메시지를 반환합니다.
	 *
	 * @return 오류 메시지
	 */
	public String getErrorMessage() {
		return responseCode.getMessage();
	}
}
