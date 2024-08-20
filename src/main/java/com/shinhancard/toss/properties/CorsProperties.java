package com.shinhancard.toss.properties;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * CORS 설정을 프로파일별로 정의하는 클래스입니다.
 * <p>
 * 설정은 application.yml 파일의 cors 프로파일에 매핑됩니다.
 * 이 클래스는 CORS (Cross-Origin Resource Sharing) 설정을 구성하여 외부 도메인에서의 요청을 제어합니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "cors")
@Validated
@Getter
@Setter
public class CorsProperties {

	/**
	 * 허용할 도메인 리스트입니다.
	 * <p>
	 * 기본값은 빈 리스트로 설정되어 있으며, 이를 통해 모든 도메인에서 요청을 허용하지 않습니다.
	 * </p>
	 */
	@NotEmpty(message = "Allowed origins cannot be empty")
	private List<String> allowedOrigins = Collections.emptyList();

	/**
	 * 허용할 HTTP 메서드 리스트입니다.
	 * <p>
	 * 기본값은 GET, POST, PUT, DELETE 메서드입니다.
	 * </p>
	 */
	@NotEmpty(message = "At least one HTTP method must be specified")
	private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE");

	/**
	 * 허용할 HTTP 헤더 리스트입니다.
	 * <p>
	 * 기본값은 Authorization과 Content-Type 헤더입니다.
	 * </p>
	 */
	@NotEmpty(message = "At least one HTTP header must be specified")
	private List<String> allowedHeaders = List.of("Authorization", "Content-Type");

	/**
	 * 자격 증명(쿠키, 인증 정보 등)을 포함한 요청 허용 여부입니다.
	 * <p>
	 * 기본값은 true로 설정되어 있으며, 자격 증명을 포함한 요청을 허용합니다.
	 * </p>
	 */
	private boolean allowCredentials = true;

	/**
	 * 설정된 값들이 유효한지 검증합니다.
	 * <p>
	 * CORS 설정이 올바르게 구성되었는지 확인하고, 필수 값이 누락된 경우 예외를 발생시킵니다.
	 * </p>
	 */
	@PostConstruct
	private void validateProperties() {
		// 허용된 메서드 리스트가 비어있는지 확인합니다.
		if (allowedMethods.isEmpty()) {
			throw new IllegalArgumentException("At least one HTTP method must be specified.");
		}
		// 허용된 헤더 리스트가 비어있는지 확인합니다.
		if (allowedHeaders.isEmpty()) {
			throw new IllegalArgumentException("At least one HTTP header must be specified.");
		}
	}
}
