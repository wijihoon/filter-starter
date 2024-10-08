package shinhancard.csrf.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 보안 헤더 설정을 위한 프로퍼티 클래스입니다.
 * <p>
 * 이 클래스는 Spring Boot의 {@link ConfigurationProperties}를 사용하여
 * 설정 파일에서 CSRF 관련 보안 헤더 값을 로드합니다. 기본값을 제공하며,
 * 애플리케이션의 설정 파일에서 값을 오버라이드할 수 있습니다.
 * </p>
 * <p>
 * {@code filter.csrf.enabled} 속성의 기본값은 {@code true}로 설정되어 있으며,
 * 이는 CSRF 필터를 기본적으로 활성화합니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "csrf")
@Getter
@Setter
public class CsrfProperties {

	/**
	 * CSRF 필터 활성화 여부입니다.
	 * <p>
	 * 기본값은 {@code true}로 설정되어 있으며, CSRF 필터를 활성화합니다.
	 * </p>
	 */
	private boolean enabled = true;

	/**
	 * X-Frame-Options 헤더의 설정값입니다.
	 * <p>
	 * 이 헤더는 클릭재킹 공격을 방지하는 데 사용됩니다. 기본값은 "DENY"입니다.
	 * </p>
	 */
	private String xFrameOptions = "DENY";

	/**
	 * X-XSS-Protection 헤더의 설정값입니다.
	 * <p>
	 * 이 헤더는 브라우저에서 XSS 공격을 방지하는 데 사용됩니다. 기본값은 "1; mode=block"입니다.
	 * </p>
	 */
	private String xXssProtection = "1; mode=block";

	/**
	 * X-Content-Type-Options 헤더의 설정값입니다.
	 * <p>
	 * 이 헤더는 MIME 스니핑 공격을 방지하는 데 사용됩니다. 기본값은 "nosniff"입니다.
	 * </p>
	 */
	private String xContentTypeOptions = "nosniff";
}
