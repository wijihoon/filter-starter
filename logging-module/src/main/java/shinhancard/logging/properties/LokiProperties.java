package shinhancard.logging.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Loki 설정을 담고 있는 객체입니다.
 * <p>
 * 이 클래스는 Loki 서버와의 연결을 위한 설정을 정의합니다.
 * 외부 구성 파일에서 설정 값을 읽어옵니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "loki")
@Validated
@Getter
@Setter
public class LokiProperties {

	/**
	 * Loki 서버의 URL.
	 * <p>
	 * 이 속성은 필수로 설정되어야 하며, null이 될 수 없습니다.
	 * </p>
	 */
	@NotNull(message = "Loki URL은 null일 수 없습니다.")
	private String url;

	// 기타 Loki 관련 설정이 필요한 경우, 여기에서 추가합니다.
	// 예: 인증 정보, 기본 레벨 등

}
