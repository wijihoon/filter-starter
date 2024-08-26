package shinhancard.sql.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

	/**
	 * SQL 인젝션 필터를 Spring 컨텍스트에 등록합니다.
	 *
	 * @param sqlInjectionProperties SQL 인젝션 필터 설정
	 * @return SQLInjectionFilter 객체
	 */
	@Bean
	public SQLInjectionFilter sqlInjectionFilter(SQLInjectionProperties sqlInjectionProperties) {
		return new SQLInjectionFilter(sqlInjectionProperties);
	}
}
