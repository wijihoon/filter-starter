package com.shinhancard.toss.exception;

import org.springframework.http.HttpStatus;

/**
 * Kafka와 관련된 오류를 처리하는 커스텀 예외 클래스입니다.
 */
public class KafkaException extends RuntimeException {
	private final ErrorCode errorCode;

	public KafkaException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public HttpStatus getHttpStatus() {
		return errorCode.getHttpStatus();
	}

	public String getErrorCode() {
		return errorCode.getCode();
	}

	public String getErrorMessage() {
		return errorCode.getMessage();
	}
}
