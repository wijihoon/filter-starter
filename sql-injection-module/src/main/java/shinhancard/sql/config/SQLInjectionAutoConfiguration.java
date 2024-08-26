package shinhancard.sql.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import shinhancard.sql.filter.SQLInjectionFilter;
import shinhancard.sql.properties.SQLInjectionProperties;

/**
 * SQL 인젝션 자동 구성을 제공하는 클래스입니다.
 * <p>
 * 이 클래스는 SQL 인젝션 필터를 자동으로 등록합니다.
 * </p>
 */
@Configuration
@EnableConfigurationProperties(SQLInjectionProperties.class)
@ConditionalOnProperty(name = "filter.sql.enabled", havingValue = "true", matchIfMissing = true)
public class SQLInjectionAutoConfiguration {

	private final SQLInjectionProperties sqlInjectionProperties;

	public SQLInjectionAutoConfiguration(SQLInjectionProperties sqlInjectionProperties) {
		this.sqlInjectionProperties = sqlInjectionProperties;
	}

	/**
	 * SQL 인젝션 필터를 Spring 컨텍스트에 등록합니다.
	 *
	 * @return SQLInjectionFilter 등록을 위한 FilterRegistrationBean 객체
	 */
	@Bean
	public FilterRegistrationBean<SQLInjectionFilter> sqlInjectionFilterRegistration() {
		FilterRegistrationBean<SQLInjectionFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SQLInjectionFilter(sqlInjectionProperties));
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2); // SQL 인젝션 필터의 순서를 설정
		return registrationBean;
	}
}
