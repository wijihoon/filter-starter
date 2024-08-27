package shinhancard.xss.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import shinhancard.xss.filter.XSSFilter;
import shinhancard.xss.properties.XSSProperties;

/**
 * XSS 자동 구성을 제공하는 클래스입니다.
 * <p>
 * 이 클래스는 XSS 필터를 자동으로 등록합니다.
 * </p>
 */
@AutoConfiguration
@EnableConfigurationProperties({XSSProperties.class})
@ConditionalOnProperty(name = "filter.xss.enabled", havingValue = "true", matchIfMissing = true)
public class XSSAutoConfiguration {

	private final XSSProperties xssProperties;

	public XSSAutoConfiguration(XSSProperties xssProperties) {
		this.xssProperties = xssProperties;
	}

	/**
	 * XSS 필터를 Spring 컨텍스트에 등록합니다.
	 *
	 * @return XSSFilter 등록을 위한 FilterRegistrationBean 객체
	 */
	@Bean
	public FilterRegistrationBean<XSSFilter> xssFilterRegistration() {
		FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new XSSFilter(xssProperties));
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 3); // XSS 필터의 순서를 설정
		return registrationBean;
	}
}
