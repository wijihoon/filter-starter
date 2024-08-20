package com.shinhancard.toss.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그 설정을 위한 프로퍼티 클래스입니다.
 * <p>
 * 이 클래스는 application.yml 또는 application.properties 파일에서 로그 관련 설정을 주입받아 처리합니다.
 * 요청 및 응답 본문, 로그 본문 크기와 관련된 설정을 포함합니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "logging")
@Validated
@Getter
@Setter
public class LoggingProperties {

	/**
	 * 요청 본문과 관련된 설정을 담고 있는 객체입니다.
	 */
	private RequestBody requestBody;

	/**
	 * 응답 본문과 관련된 설정을 담고 있는 객체입니다.
	 */
	private ResponseBody responseBody;

	/**
	 * 로그 본문과 관련된 설정을 담고 있는 객체입니다.
	 */
	@NotNull(message = "Body configuration cannot be null") // body 필드는 null이 될 수 없습니다.
	private Body body;

	/**
	 * 클래스 초기화 후 설정을 검증하는 메서드입니다.
	 * <p>
	 * 로그 본문 크기 설정이 유효한지 검증합니다. 본문 크기는 0보다 커야 합니다.
	 * </p>
	 */
	private void validateProperties() {
		// 로그 본문 크기가 0 이하인 경우 예외를 발생시킵니다.
		if (body.getMaxSize() <= 0) {
			throw new IllegalArgumentException("Max size for log body must be greater than zero.");
		}
	}

	/**
	 * 요청 본문과 관련된 설정을 위한 내부 클래스입니다.
	 */
	@Getter
	@Setter
	public static class RequestBody {
		private boolean truncate;
	}

	/**
	 * 응답 본문과 관련된 설정을 위한 내부 클래스입니다.
	 */
	@Getter
	@Setter
	public static class ResponseBody {
		private boolean truncate;
	}

	/**
	 * 로그 본문 크기와 관련된 설정을 위한 내부 클래스입니다.
	 */
	@Getter
	@Setter
	public static class Body {
		@Min(value = 1, message = "Max size for log body must be greater than zero.") // 최소값 검증
		private int maxSize;
	}
}
