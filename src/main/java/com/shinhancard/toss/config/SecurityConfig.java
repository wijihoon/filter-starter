package com.shinhancard.toss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;

import com.shinhancard.toss.filter.CorsFilter;
import com.shinhancard.toss.filter.XssFilter;

import lombok.RequiredArgsConstructor;

/**
 * 애플리케이션의 보안 설정을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 JWT 토큰 발급, CORS 필터 및 XSS 필터를 설정하며,
 * API의 세션 관리 및 접근 제어를 구성합니다.
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsFilter corsFilter; // CORS 설정 주입
	private final XssFilter xssFilter; // XSS 필터 주입

	/**
	 * 보안 필터 체인을 구성합니다.
	 *
	 * @param http HttpSecurity 객체
	 * @return 설정된 SecurityFilterChain 객체
	 * @throws Exception 보안 설정 시 발생할 수 있는 예외
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화합니다. API 기반 애플리케이션에서 일반적입니다.
			.addFilterBefore(corsFilter.corsFilter(),
				UsernamePasswordAuthenticationFilter.class) // CORS 필터를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
			.addFilterBefore(xssFilter,
				UsernamePasswordAuthenticationFilter.class) // XSS 필터를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers(
						"/oauth/token",
						"/swagger-ui.html",
						"/swagger-ui/**",
						"/v3/api-docs/**"
					)
					.permitAll() // /api/login 경로는 인증 없이 접근을 허용합니다.
					.anyRequest()
					.authenticated() // 나머지 모든 요청은 인증이 필요합니다.
			)
			.headers(headers -> headers
				.addHeaderWriter(new ContentSecurityPolicyHeaderWriter(
					"default-src 'self'; " + // 기본 소스는 자신만 허용합니다.
						"script-src 'self'; " + // 스크립트 소스는 자신만 허용합니다.
						"style-src 'self'; " + // 스타일 소스는 자신만 허용합니다.
						"object-src 'none'; " + // 객체 소스는 허용하지 않습니다.
						"frame-ancestors 'none';")) // 프레임 조상은 허용하지 않습니다.
			)
			.sessionManagement(sessionManagement ->
				sessionManagement
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 무상태로 설정합니다. (REST API에서 일반적)
			);

		return http.build(); // 설정된 보안 필터 체인을 반환합니다.
	}
}
