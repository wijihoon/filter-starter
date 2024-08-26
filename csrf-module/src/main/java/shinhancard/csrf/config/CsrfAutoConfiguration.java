package shinhancard.csrf.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import shinhancard.csrf.filter.CsrfFilter;
import shinhancard.csrf.properties.CsrfProperties;

/**
 * CSRF(크로스 사이트 요청 위조) 필터와 관련된 설정을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 Spring의 {@link Configuration} 어노테이션을 사용하여 CSRF 필터를 빈으로 등록합니다.
 * </p>
 */
@Configuration
@ConditionalOnProperty(name = "filter.csrf.enabled", havingValue = "true", matchIfMissing = true)
public class CsrfAutoConfiguration {

	private final CsrfProperties csrfProperties;

	public CsrfAutoConfiguration(CsrfProperties csrfProperties) {
		this.csrfProperties = csrfProperties;
	}

	@Bean
	public FilterRegistrationBean<CsrfFilter> csrfFilterRegistration() {
		FilterRegistrationBean<CsrfFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new CsrfFilter(csrfProperties));
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 4); // CSRF 필터의 순서를 설정
		return registrationBean;
	}
}
