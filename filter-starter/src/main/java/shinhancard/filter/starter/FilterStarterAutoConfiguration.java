package shinhancard.filter.starter;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import shinhancard.cors.config.CorsAutoConfiguration;
import shinhancard.csrf.config.CsrfAutoConfiguration;
import shinhancard.logging.config.LogAutoConfiguration;
import shinhancard.sql.config.SQLInjectionAutoConfiguration;
import shinhancard.xss.config.XSSAutoConfiguration;

/**
 * 다양한 보안 및 로깅 필터를 자동으로 구성하는 설정 클래스입니다.
 * <p>
 * 이 클래스는 Spring Boot의 자동 구성 메커니즘을 통해 필요한 필터들을 등록합니다.
 * 등록된 필터들은 조건에 따라 동적으로 활성화되며, 외부 설정에 따라 동작을 조절할 수 있습니다.
 * </p>
 */
@AutoConfigureAfter({
	CorsAutoConfiguration.class,
	LogAutoConfiguration.class,
	CsrfAutoConfiguration.class,
	XSSAutoConfiguration.class,
	SQLInjectionAutoConfiguration.class
})
public class FilterStarterAutoConfiguration {
}
