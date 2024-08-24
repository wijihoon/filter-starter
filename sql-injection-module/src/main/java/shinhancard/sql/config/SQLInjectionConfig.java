package shinhancard.sql.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shinhancard.sql.filter.SQLInjectionFilter;
import shinhancard.sql.properties.SQLInjectionProperties;

/**
 * SQL 인젝션 필터와 관련된 설정을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 Spring의 {@link Configuration} 어노테이션을 사용하여 SQL 인젝션 필터를 빈으로 등록합니다.
 * </p>
 */
@Configuration
public class SQLInjectionConfig {

    /**
     * SQL 인젝션 필터 빈을 생성하여 Spring 컨텍스트에 등록합니다.
     *
     * @param sqlInjectionProperties SQL 인젝션 검사에 필요한 설정 정보를 담고 있는 {@link SQLInjectionProperties} 객체
     * @return SQL 인젝션 공격을 탐지하고 방지하는 {@link SQLInjectionFilter} 객체
     */
    @Bean
    public SQLInjectionFilter sqlInjectionFilter(SQLInjectionProperties sqlInjectionProperties) {
        return new SQLInjectionFilter(sqlInjectionProperties);
    }
}
