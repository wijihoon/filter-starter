package shinhancard.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 * 보안 헤더 설정을 위한 프로퍼티 클래스입니다.
 * 이 클래스는 기본값을 제공하며, 설정 파일에서 값을 오버라이드할 수 있습니다.
 */
@Configuration
@ConfigurationProperties(prefix = "csrf.headers")
@Getter
@Setter
public class CsrfProperties {

	private String xFrameOptions = "DENY"; // 기본값: DENY
	private String xXssProtection = "1; mode=block"; // 기본값: 1; mode=block
	private String xContentTypeOptions = "nosniff"; // 기본값: nosniff
}
