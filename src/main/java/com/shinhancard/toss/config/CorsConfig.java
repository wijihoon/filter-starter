package com.shinhancard.toss.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.shinhancard.toss.properties.CorsProperties;

import lombok.RequiredArgsConstructor;

/**
 * CORS 설정을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 CORS 요청에 대한 설정을 구성하고 반환합니다.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

	private final CorsProperties corsProperties; // CORS 설정을 주입받습니다.

	/**
	 * CORS 설정을 반환하는 빈을 정의합니다.
	 *
	 * @return CORS 설정을 포함하는 CorsConfigurationSource 객체
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowCredentials(corsProperties.isAllowCredentials());
		corsConfig.setAllowedOrigins(
			corsProperties.getAllowedOrigins().isEmpty()
				? Collections.singletonList("*")
				: corsProperties.getAllowedOrigins()
		);
		corsConfig.setAllowedMethods(corsProperties.getAllowedMethods());
		corsConfig.setAllowedHeaders(corsProperties.getAllowedHeaders());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}
}
