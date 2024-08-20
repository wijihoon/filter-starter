package com.shinhancard.toss.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
	private final ErrorCode errorCode;

	public InvalidTokenException(ErrorCode errorCode) {
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
