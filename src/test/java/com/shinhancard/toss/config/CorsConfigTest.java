package com.shinhancard.toss.config;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.shinhancard.toss.properties.CorsProperties;

/**
 * CORS 설정을 테스트하는 클래스입니다.
 * <p>
 * 다양한 CORS 설정이 올바르게 적용되는지 검증합니다.
 * </p>
 */
@SpringBootTest
public class CorsConfigTest {

	@Autowired
	private WebApplicationContext webApplicationContext; // 웹 애플리케이션 컨텍스트

	@MockBean
	private CorsProperties corsProperties; // CORS 프로퍼티를 모킹합니다.

	private MockMvc mockMvc; // MockMvc 객체를 사용하여 웹 요청을 테스트합니다.

	@BeforeEach
	public void setUp() {
		// MockMvc 객체를 설정합니다.
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		setupCorsProperties(true, Arrays.asList("http://example.com", "http://example.org"),
			Arrays.asList("GET", "POST"),
			Arrays.asList("Authorization", "Content-Type"));
	}

	/**
	 * CORS 설정이 기본값일 때의 필터 테스트입니다.
	 * <p>
	 * 기본값이 적용된 CORS 설정이 올바르게 동작하는지 검증합니다.
	 * </p>
	 */
	@Test
	@DisplayName("기본 CORS 설정 검증")
	public void corsFilterTest_defaultSettings() throws Exception {
		setupCorsProperties(true, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

		mockMvc.perform(options("/").header("Origin", "http://example.com"))
			.andExpect(status().isOk())
			.andExpect(header().string("Access-Control-Allow-Origin", "*"))
			.andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")))
			.andExpect(header().string("Access-Control-Allow-Headers", containsString("Authorization")));
	}

	/**
	 * 커스텀 CORS 설정이 적용된 경우의 필터 테스트입니다.
	 * <p>
	 * 커스텀 설정이 올바르게 반영되는지 검증합니다.
	 * </p>
	 */
	@Test
	@DisplayName("커스텀 CORS 설정 검증")
	public void corsFilterTest_customSettings() throws Exception {
		setupCorsProperties(true, Arrays.asList("http://example.com", "http://example.org"),
			Arrays.asList("GET", "POST"),
			Arrays.asList("Authorization", "Content-Type"));

		mockMvc.perform(options("/").header("Origin", "http://example.com"))
			.andExpect(status().isOk())
			.andExpect(header().string("Access-Control-Allow-Origin", "http://example.com"))
			.andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")))
			.andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")));
	}

	/**
	 * 자격 증명 포함 여부가 false인 경우의 필터 테스트입니다.
	 * <p>
	 * 자격 증명 포함 여부가 false일 때 CORS 설정이 올바르게 반영되는지 검증합니다.
	 * </p>
	 */
	@Test
	@DisplayName("자격 증명 포함 여부가 false일 때 CORS 설정 검증")
	public void corsFilterTest_noCredentials() throws Exception {
		setupCorsProperties(false, List.of("http://example.com"),
			List.of("GET"),
			List.of("Authorization"));

		mockMvc.perform(options("/").header("Origin", "http://example.com"))
			.andExpect(status().isOk())
			.andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
	}

	/**
	 * 유효하지 않은 도메인 설정이 적용된 경우의 필터 테스트입니다.
	 * <p>
	 * 유효하지 않은 도메인 설정이 반영되는지 검증합니다.
	 * </p>
	 */
	@Test
	@DisplayName("유효하지 않은 도메인 설정 CORS 필터 테스트")
	public void corsFilterTest_invalidOrigins() throws Exception {
		setupCorsProperties(true, List.of("http://invalid.com"),
			Arrays.asList("PUT", "PATCH"),
			Arrays.asList("Content-Type", "Accept"));

		mockMvc.perform(options("/").header("Origin", "http://invalid.com"))
			.andExpect(status().isOk())
			.andExpect(header().string("Access-Control-Allow-Origin", "http://invalid.com"))
			.andExpect(header().string("Access-Control-Allow-Methods", containsString("PUT")))
			.andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")));
	}

	/**
	 * CORS 프로퍼티를 설정합니다.
	 *
	 * @param allowCredentials 자격 증명 허용 여부
	 * @param allowedOrigins   허용된 도메인 리스트
	 * @param allowedMethods   허용된 HTTP 메서드 리스트
	 * @param allowedHeaders   허용된 헤더 리스트
	 */
	private void setupCorsProperties(boolean allowCredentials, List<String> allowedOrigins,
		List<String> allowedMethods, List<String> allowedHeaders) {
		// 모킹된 CorsProperties 객체에 설정값을 주입합니다.
		when(corsProperties.isAllowCredentials()).thenReturn(allowCredentials);
		when(corsProperties.getAllowedOrigins()).thenReturn(allowedOrigins);
		when(corsProperties.getAllowedMethods()).thenReturn(allowedMethods);
		when(corsProperties.getAllowedHeaders()).thenReturn(allowedHeaders);
	}
}