package com.shinhancard.toss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;

import com.shinhancard.toss.filter.XssFilter;
import com.shinhancard.toss.security.JwtAuthenticationFilter;
import com.shinhancard.toss.security.JwtTokenService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 애플리케이션의 보안 설정을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 OAuth2 인증, JWT 토큰 인증, CORS 필터 및 XSS 필터를 설정하며,
 * API의 세션 관리 및 접근 제어를 구성합니다.
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenService jwtTokenService; // JWT 토큰 서비스 주입
	private final CorsConfig corsConfig; // CORS 설정 주입
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
			.addFilterBefore(corsConfig.corsFilter(),
				UsernamePasswordAuthenticationFilter.class) // CORS 필터를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
			.addFilterBefore(xssFilter,
				UsernamePasswordAuthenticationFilter.class) // XSS 필터를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers(
							"/api/login-and-redirect",
							"/api/login-with-cookie",
							"/api/login-with-header",
							"/api/login-with-body",
							"/swagger-ui.html",
							"/swagger-ui/**",
							"/v3/api-docs/**"
					)
					.permitAll() // /api/login 경로는 인증 없이 접근을 허용합니다.
					.anyRequest()
					.authenticated() // 나머지 모든 요청은 인증이 필요합니다.
			)
			.addFilterBefore(new JwtAuthenticationFilter("/api/**", jwtTokenService),
				UsernamePasswordAuthenticationFilter.class) // JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
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
			)
			.exceptionHandling(exceptionHandling ->
				exceptionHandling
					.authenticationEntryPoint((request, response, authException) -> {
						// 인증 실패 시 처리 로직
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다."); // 401 응답을 클라이언트에 전송합니다.
					})
					.accessDeniedHandler((request, response, accessDeniedException) -> {
						// 권한 부족 시 처리 로직
						response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근이 거부되었습니다."); // 403 응답을 클라이언트에 전송합니다.
					})
			);

		return http.build(); // 설정된 보안 필터 체인을 반환합니다.
	}

	/**
	 * OAuth2 사용자 서비스를 설정합니다.
	 *
	 * @return OAuth2UserService 객체
	 */
	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
		return new DefaultOAuth2UserService(); // 기본 OAuth2 사용자 서비스 객체를 반환합니다.
	}
}
