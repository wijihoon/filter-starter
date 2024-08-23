package shinhancard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shinhancard.properties.CorsProperties;

/**
 * CORS 설정을 위한 구성 클래스입니다.
 */
@Configuration
public class CorsConfig {

	private final CorsProperties corsProperties;

	public CorsConfig(CorsProperties corsProperties) {
		this.corsProperties = corsProperties;
	}

	/**
	 * CORS 설정을 반환하는 빈을 생성합니다.
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
