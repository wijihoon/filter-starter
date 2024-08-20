package com.shinhancard.toss.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.shinhancard.toss.properties.CorsProperties;

import lombok.RequiredArgsConstructor;

/**
 * CORS 설정을 적용하는 클래스입니다.
 * <p>
 * {@link CorsProperties} 클래스로부터 CORS 설정을 주입받아 {@link CorsFilter}를 설정합니다.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

	// CORS 설정을 위한 프로퍼티 주입
	private final CorsProperties corsProperties;

	/**
	 * CORS 설정을 적용한 {@link CorsFilter}를 반환합니다.
	 *
	 * @return CORS 설정이 적용된 {@link CorsFilter} 객체
	 */
	@Bean
	public CorsFilter corsFilter() {
		// CORS 설정을 위한 CorsConfiguration 객체 생성
		CorsConfiguration config = new CorsConfiguration();

		// 자격 증명 포함 여부 설정
		config.setAllowCredentials(corsProperties.isAllowCredentials());

		// 허용된 도메인 설정, 빈 리스트일 경우 모든 도메인 허용
		List<String> allowedOrigins = corsProperties.getAllowedOrigins();
		config.setAllowedOrigins(allowedOrigins.isEmpty() ? Collections.singletonList("*") : allowedOrigins);

		// 허용된 HTTP 메서드 설정
		config.setAllowedMethods(corsProperties.getAllowedMethods());

		// 허용된 헤더 설정
		config.setAllowedHeaders(corsProperties.getAllowedHeaders());

		// CORS 설정을 URL 경로와 매핑
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 설정 적용

		// CorsFilter 객체 반환
		return new CorsFilter(source);
	}
}
