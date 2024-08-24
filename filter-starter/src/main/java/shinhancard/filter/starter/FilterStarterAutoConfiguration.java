package shinhancard.filter.starter;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@AutoConfigureAfter(name = {
        "shinhancard.cors.config.CorsConfig",
        "shinhancard.logging.config.LogConfig",
        "shinhancard.csrf.config.CsrfConfig",
        "shinhancard.xss.filter.XSSFilter",
        "shinhancard.sql.filter.SQLInjectionFilter" // 추가된 부분
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

    @Bean
    @ConditionalOnMissingBean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public LogFilter logFilter() {
        return new LogFilter(logProperties, logService);
    }

    @Bean
    @ConditionalOnMissingBean
    public CsrfConfig csrfConfig() {
        return new CsrfConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public XSSFilter xssFilter() {
        return new XSSFilter(xssProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SQLInjectionFilter sqlInjectionFilter() {
        return new SQLInjectionFilter(sqlInjectionProperties); // 추가된 부분
    }
}
