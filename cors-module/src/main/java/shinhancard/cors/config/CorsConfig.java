package shinhancard.cors.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shinhancard.cors.filter.CorsFilter;
import shinhancard.cors.properties.CorsProperties;

/**
 * CORS (Cross-Origin Resource Sharing) 설정을 위한 구성 클래스입니다.
 * <p>
 * 이 클래스는 CORS 설정을 구성하고, Spring의 {@link CorsConfigurationSource} 빈을 생성하여
 * HTTP 요청에 대해 CORS 정책을 적용합니다. {@link CorsProperties}에서 설정 값을 로드하여
 * CORS 정책을 동적으로 구성할 수 있습니다.
 * </p>
 */
@Configuration
public class CorsConfig {

    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        if (corsProperties.getAllowedOrigins() != null) {
            corsConfiguration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        }

        if (corsProperties.getAllowedMethods() != null) {
            corsConfiguration.setAllowedMethods(corsProperties.getAllowedMethods());
        }

        if (corsProperties.getAllowedHeaders() != null) {
            corsConfiguration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        }

        corsConfiguration.setAllowCredentials(corsProperties.isAllowCredentials());
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(CorsConfigurationSource corsConfigurationSource) {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorsFilter(corsConfigurationSource));
        registrationBean.setOrder(0); // 가장 먼저 실행되도록 설정
        return registrationBean;
    }
}
