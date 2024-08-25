package shinhancard.filter.starter;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfigurationSource;
import shinhancard.cors.filter.CorsFilter;
import shinhancard.csrf.config.CsrfConfig;
import shinhancard.logging.filter.LogFilter;
import shinhancard.logging.properties.LogProperties;
import shinhancard.logging.service.LogService;
import shinhancard.sql.filter.SQLInjectionFilter;
import shinhancard.sql.properties.SQLInjectionProperties;
import shinhancard.xss.filter.XSSFilter;
import shinhancard.xss.properties.XSSProperties;

/**
 * 다양한 보안 및 로깅 필터를 자동으로 구성하는 설정 클래스입니다.
 * <p>
 * 이 클래스는 Spring Boot의 자동 구성 메커니즘을 통해 필요한 필터들을 등록합니다.
 * 등록된 필터들은 조건에 따라 동적으로 활성화되며, 외부 설정에 따라 동작을 조절할 수 있습니다.
 * </p>
 */
@Configuration
@AutoConfigureAfter(name = {
        "shinhancard.cors.config.CorsConfig",
        "shinhancard.logging.config.LogConfig",
        "shinhancard.csrf.config.CsrfConfig",
        "shinhancard.xss.filter.XSSFilter",
        "shinhancard.sql.filter.SQLInjectionFilter"
})
public class FilterStarterAutoConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;
    private final LogProperties logProperties;
    private final LogService logService;
    private final XSSProperties xssProperties;
    private final SQLInjectionProperties sqlInjectionProperties;

    public FilterStarterAutoConfiguration(
            CorsConfigurationSource corsConfigurationSource,
            LogProperties logProperties,
            LogService logService,
            XSSProperties xssProperties,
            SQLInjectionProperties sqlInjectionProperties) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.logProperties = logProperties;
        this.logService = logService;
        this.xssProperties = xssProperties;
        this.sqlInjectionProperties = sqlInjectionProperties;
    }

    /**
     * CORS 필터를 빈으로 등록하고 순서를 설정합니다.
     * <p>
     * 필터가 설정된 순서에 따라 요청이 처리됩니다.
     * </p>
     *
     * @return FilterRegistrationBean CORS 필터 등록 빈
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorsFilter(corsConfigurationSource));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // CORS 필터를 가장 먼저 실행되도록 설정
        return registrationBean;
    }

    /**
     * 로그 필터를 빈으로 등록하고 순서를 설정합니다.
     * <p>
     * 필터가 설정된 순서에 따라 요청이 처리됩니다.
     * </p>
     *
     * @return FilterRegistrationBean 로그 필터 등록 빈
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<LogFilter> logFilterRegistration() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter(logProperties, logService));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // CORS 필터 다음으로 실행되도록 설정
        return registrationBean;
    }

    /**
     * CSRF 설정을 빈으로 등록합니다.
     * <p>
     * csrfConfig 빈이 없을 경우 이 메서드가 호출되어 CSRF 설정을 등록합니다.
     * </p>
     *
     * @return CsrfConfig CSRF 설정 객체
     */
    @Bean
    @ConditionalOnMissingBean
    public CsrfConfig csrfConfig() {
        return new CsrfConfig();
    }

    /**
     * XSS 필터를 빈으로 등록하고 순서를 설정합니다.
     * <p>
     * 필터가 설정된 순서에 따라 요청이 처리됩니다.
     * </p>
     *
     * @return FilterRegistrationBean XSS 필터 등록 빈
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<XSSFilter> xssFilterRegistration() {
        FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XSSFilter(xssProperties));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2); // 로그 필터 다음으로 실행되도록 설정
        return registrationBean;
    }

    /**
     * SQL 인젝션 필터를 빈으로 등록하고 순서를 설정합니다.
     * <p>
     * 필터가 설정된 순서에 따라 요청이 처리됩니다.
     * </p>
     *
     * @return FilterRegistrationBean SQL 인젝션 필터 등록 빈
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<SQLInjectionFilter> sqlInjectionFilterRegistration() {
        FilterRegistrationBean<SQLInjectionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SQLInjectionFilter(sqlInjectionProperties));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 3); // XSS 필터 다음으로 실행되도록 설정
        return registrationBean;
    }
}
