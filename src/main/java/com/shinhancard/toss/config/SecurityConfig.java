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
import org.springframework.web.cors.CorsConfigurationSource;

import com.shinhancard.toss.filter.CustomCorsFilter;
import com.shinhancard.toss.filter.XssFilter;
import com.shinhancard.toss.properties.XssProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsConfigurationSource corsConfigurationSource;
	private final XssProperties xssProperties;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.addFilterBefore(new CustomCorsFilter(corsConfigurationSource), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new XssFilter(xssProperties), UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers(
						"/oauth/token",
						"/swagger-ui.html",
						"/swagger-ui/**",
						"/v3/api-docs/**"
					)
					.permitAll()
					.anyRequest()
					.authenticated()
			)
			.headers(headers -> headers
				.addHeaderWriter(new ContentSecurityPolicyHeaderWriter(
					"default-src 'self'; " +
						"script-src 'self'; " +
						"style-src 'self'; " +
						"object-src 'none'; " +
						"frame-ancestors 'none';"))
			)
			.sessionManagement(sessionManagement ->
				sessionManagement
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);

		return http.build();
	}
}
