package shinhancard.cors.properties;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * CORS (Cross-Origin Resource Sharing) 설정을 프로파일별로 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 Spring Boot의 `application.yml` 파일에서 "filter.cors" 프로파일에 매핑된 설정을 관리합니다.
 * CORS 설정을 통해 외부 도메인에서의 요청을 제어하며, 필요한 경우 요청을 허용하거나 차단할 수 있습니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "filter.cors")
@Getter
@Setter
public class CorsProperties {

	/**
	 * 허용할 도메인 목록입니다.
	 * <p>
	 * 이 목록에 포함된 도메인에서 오는 요청을 허용합니다. 기본값은 모든 도메인을 허용하도록 설정되어 있습니다.
	 * "*"을 설정하면 모든 도메인에서의 요청을 허용합니다.
	 * </p>
	 */
	private List<String> allowedOrigins = Collections.singletonList("*");

	/**
	 * 허용할 HTTP 메서드 목록입니다.
	 * <p>
	 * 이 목록에 포함된 HTTP 메서드만 허용합니다. 기본값은 모든 메서드를 허용하도록 설정되어 있습니다.
	 * "*"을 설정하면 모든 HTTP 메서드를 허용합니다.
	 * </p>
	 */
	private List<String> allowedMethods = Collections.singletonList("*");

	/**
	 * 허용할 HTTP 헤더 목록입니다.
	 * <p>
	 * 이 목록에 포함된 헤더만 허용됩니다. 기본값은 모든 헤더를 허용하도록 설정되어 있습니다.
	 * "*"을 설정하면 모든 HTTP 헤더를 허용합니다.
	 * </p>
	 */
	private List<String> allowedHeaders = Collections.singletonList("*");

	/**
	 * 자격 증명(쿠키, 인증 정보 등)을 포함한 요청 허용 여부입니다.
	 * <p>
	 * 자격 증명을 포함한 요청을 허용할지 여부를 설정합니다. 기본값은 true로 설정되어 있으며, 자격 증명을 포함한 요청을 허용합니다.
	 * </p>
	 */
	private boolean allowCredentials = true;
}
