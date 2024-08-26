package shinhancard.xss.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shinhancard.xss.filter.XSSFilter;
import shinhancard.xss.properties.XSSProperties;

/**
 * XSS 자동 구성을 제공하는 클래스입니다.
 * <p>
 * 이 클래스는 XSS 필터를 자동으로 등록합니다.
 * </p>
 */
@Configuration
@EnableConfigurationProperties(XSSProperties.class)
@ConditionalOnProperty(name = "filter.xss.enabled", havingValue = "true", matchIfMissing = true)
public class XSSAutoConfiguration {

	/**
	 * XSS 필터를 Spring 컨텍스트에 등록합니다.
	 *
	 * @param xssProperties XSS 필터 설정
	 * @return XSSFilter 객체
	 */
	@Bean
	public XSSFilter xssFilter(XSSProperties xssProperties) {
		return new XSSFilter(xssProperties);
	}
}
