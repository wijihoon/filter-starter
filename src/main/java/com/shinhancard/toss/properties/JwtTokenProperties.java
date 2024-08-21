package com.shinhancard.toss.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * JWT 관련 설정을 관리하는 클래스입니다.
 * <p>
 * 이 클래스는 application.yml 파일에서 JWT 관련 설정을 읽어옵니다.
 * JWT 비밀 키, 토큰 유효 기간 및 리프레시 토큰 유효 기간을 설정할 수 있습니다.
 * </p>
 */
@Configuration
@Getter // Lombok의 @Getter 어노테이션을 사용하여 모든 필드의 getter 메서드를 자동으로 생성합니다.
@Setter // Lombok의 @Setter 어노테이션을 사용하여 모든 필드의 setter 메서드를 자동으로 생성합니다.
@ConfigurationProperties(prefix = "jwt") // application.yml의 jwt 접두사에 매핑되는 설정을 읽어옵니다.
@Validated // 유효성 검사를 활성화합니다.
public class JwtTokenProperties {

	/**
	 * JWT 비밀 키.
	 * <p>
	 * 기본값은 "defaultSecretKey"입니다.
	 * </p>
	 */
	private String secret = "defaultSecretKey"; // 기본값 설정

	/**
	 * JWT 토큰 유효 기간 (밀리초 단위).
	 * <p>
	 * 기본값은 3600000ms (1시간)입니다.
	 * </p>
	 */
	@Positive(message = "Validity must be greater than 0") // 유효 기간이 0보다 커야 합니다.
	private int validity = 3600000; // 기본값은 1시간 (3600000ms)

	/**
	 * JWT 리프레시 토큰 유효 기간 (밀리초 단위).
	 * <p>
	 * 기본값은 86400000ms (24시간)입니다.
	 * </p>
	 */
	@Positive(message = "Refresh validity must be greater than 0") // 유효 기간이 0보다 커야 합니다.
	private int refreshValidity = 86400000; // 기본값은 24시간 (86400000ms)

	/**
	 * 설정된 값들이 유효한지 검증합니다.
	 * <p>
	 * validity와 refreshValidity 값이 0 이하로 설정되지 않도록 합니다.
	 * </p>
	 */
	@PostConstruct
	private void validateProperties() {
		// validity와 refreshValidity가 0 이하인 경우 예외를 발생시킵니다.
		if (validity <= 0 || refreshValidity <= 0) {
			throw new IllegalArgumentException("JWT validity and refreshValidity must be greater than 0");
		}
	}
}
