package com.shinhancard.toss.properties;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
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
@Getter
@Setter
public class CorsProperties {

	/**
	 * 허용할 도메인 리스트입니다.
	 * <p>
	 * 기본값은 모든 도메인을 허용하도록 설정되어 있습니다.
	 * </p>
	 */
	private List<String> allowedOrigins = Collections.singletonList("*");

	/**
	 * 허용할 HTTP 메서드 리스트입니다.
	 * <p>
	 * 기본값은 모든 메서드를 허용하도록 설정되어 있습니다.
	 * </p>
	 */
	private List<String> allowedMethods = Collections.singletonList("*");

	/**
	 * 허용할 HTTP 헤더 리스트입니다.
	 * <p>
	 * 기본값은 모든 헤더를 허용하도록 설정되어 있습니다.
	 * </p>
	 */
	private List<String> allowedHeaders = Collections.singletonList("*");

	/**
	 * 자격 증명(쿠키, 인증 정보 등)을 포함한 요청 허용 여부입니다.
	 * <p>
	 * 기본값은 true로 설정되어 있으며, 자격 증명을 포함한 요청을 허용합니다.
	 * </p>
	 */
	private boolean allowCredentials = true;

	/**
	 * 검증 활성화 여부입니다.
	 * <p>
	 * 기본값은 false로 설정되어 있으며, 검증을 수행하지않는다.
	 * </p>
	 */
	private boolean enable = false;

	/**
	 * 설정된 값들이 유효한지 검증합니다.
	 * <p>
	 * enableValidation이 true일 때만 검증을 수행합니다.
	 * </p>
	 */
	@PostConstruct
	private void validateProperties() {
		if (enable) {
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
}
