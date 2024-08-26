package shinhancard.cors.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import shinhancard.cors.filter.CorsFilter;
import shinhancard.cors.properties.CorsProperties;

/**
 * CORS (Cross-Origin Resource Sharing) 자동 구성을 제공하는 클래스입니다.
 * <p>
 * 이 클래스는 CORS 설정을 자동으로 등록하여 CORS 요청을 처리할 수 있도록 구성합니다.
 * </p>
 */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
@ConditionalOnProperty(name = "filter.cors.enabled", havingValue = "true", matchIfMissing = true)
public class CorsAutoConfiguration {

	private final CorsProperties corsProperties;

	/**
	 * {@link CorsAutoConfiguration}의 생성자입니다.
	 * <p>
	 * CORS 설정을 담고 있는 {@link CorsProperties} 객체를 주입받아 초기화합니다.
	 * </p>
	 *
	 * @param corsProperties CORS 설정을 담고 있는 {@link CorsProperties} 객체
	 */
	public CorsAutoConfiguration(CorsProperties corsProperties) {
		this.corsProperties = corsProperties;
	}

	/**
	 * CORS 설정을 구성하는 {@link CorsConfigurationSource} 빈을 생성합니다.
	 * <p>
	 * {@link CorsConfigurationSource}는 CORS 설정을 제공하며, 요청 경로에 대해 CORS 설정을 적용합니다.
	 * </p>
	 *
	 * @return CORS 설정을 제공하는 {@link CorsConfigurationSource} 객체
	 */
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

	/**
	 * CORS 필터를 등록하는 {@link FilterRegistrationBean} 빈을 생성합니다.
	 * <p>
	 * {@link FilterRegistrationBean}은 CORS 필터를 등록하고, 필터의 실행 순서를 설정합니다.
	 * </p>
	 *
	 * @param corsConfigurationSource CORS 설정을 제공하는 {@link CorsConfigurationSource} 객체
	 * @return CORS 필터를 등록하는 {@link FilterRegistrationBean} 객체
	 */
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilterRegistration(CorsConfigurationSource corsConfigurationSource) {
		FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new CorsFilter(corsConfigurationSource));
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 가장 먼저 실행되도록 설정
		return registrationBean;
	}
}
