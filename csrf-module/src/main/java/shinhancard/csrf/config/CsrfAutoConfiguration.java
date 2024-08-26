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
 * {@code filter.csrf.enabled} 속성이 {@code true}로 설정된 경우에만 CSRF 필터가 활성화됩니다.
 * </p>
 */
@Configuration
@ConditionalOnProperty(name = "filter.csrf.enabled", havingValue = "true", matchIfMissing = true)
public class CsrfAutoConfiguration {

	private final CsrfProperties csrfProperties;

	/**
	 * {@link CsrfAutoConfiguration}의 생성자입니다.
	 * <p>
	 * CSRF 설정을 담고 있는 {@link CsrfProperties} 객체를 주입받아 초기화합니다.
	 * </p>
	 *
	 * @param csrfProperties CSRF 설정을 담고 있는 {@link CsrfProperties} 객체
	 */
	public CsrfAutoConfiguration(CsrfProperties csrfProperties) {
		this.csrfProperties = csrfProperties;
	}

	/**
	 * CSRF 필터를 등록하는 {@link FilterRegistrationBean} 빈을 생성합니다.
	 * <p>
	 * {@link FilterRegistrationBean}을 사용하여 CSRF 필터를 등록하고, 필터의 실행 순서를 설정합니다.
	 * </p>
	 *
	 * @return CSRF 필터를 등록하는 {@link FilterRegistrationBean} 객체
	 */
	@Bean
	public FilterRegistrationBean<CsrfFilter> csrfFilterRegistration() {
		FilterRegistrationBean<CsrfFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new CsrfFilter(csrfProperties));
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 4); // CSRF 필터의 순서를 설정
		return registrationBean;
	}
}
