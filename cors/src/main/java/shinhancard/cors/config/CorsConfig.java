package shinhancard.cors.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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

    /**
     * 생성자 주입을 통해 {@link CorsProperties}를 설정합니다.
     *
     * @param corsProperties CORS 설정을 위한 프로퍼티 객체
     */
    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    /**
     * CORS 설정을 반환하는 빈을 생성합니다.
     * <p>
     * 이 메소드는 {@link CorsConfigurationSource} 빈을 생성하여
     * CORS 정책을 구성합니다. {@link CorsConfiguration} 객체를 생성하고,
     * {@link CorsProperties}에서 설정 값을 로드하여 각 CORS 속성을 구성합니다.
     * </p>
     *
     * @return CORS 설정 소스
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 허용된 출처 설정
        if (corsProperties.getAllowedOrigins() != null) {
            corsConfiguration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        }

        // 허용된 HTTP 메서드 설정
        if (corsProperties.getAllowedMethods() != null) {
            corsConfiguration.setAllowedMethods(corsProperties.getAllowedMethods());
        }

        // 허용된 HTTP 헤더 설정
        if (corsProperties.getAllowedHeaders() != null) {
            corsConfiguration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        }

        // 자격 증명 허용 여부 설정
        corsConfiguration.setAllowCredentials(corsProperties.isAllowCredentials());

        // 모든 경로에 대해 CORS 설정 적용
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
