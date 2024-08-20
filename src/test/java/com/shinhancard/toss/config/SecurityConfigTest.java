package com.shinhancard.toss.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * 보안 설정을 테스트하는 클래스입니다.
 * <p>
 * 이 클래스는 SecurityConfig 설정이 올바르게 적용되었는지 검증합니다.
 * </p>
 */
@WebMvcTest
public class SecurityConfigTest {

	@Autowired
	private WebApplicationContext context; // 웹 애플리케이션 컨텍스트 주입

	private MockMvc mockMvc; // MockMvc 객체

	/**
	 * 테스트를 위한 MockMvc 설정을 수행합니다.
	 */
	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build(); // MockMvc 객체를 빌드합니다.
	}

	/**
	 * 인증되지 않은 사용자가 접근할 수 있는 경로를 테스트합니다.
	 *
	 * @throws Exception 테스트 중 발생할 수 있는 예외
	 */
	@Test
	public void whenNotAuthenticated_thenAccessible() throws Exception {
		mockMvc.perform(get("/api/login")) // /api/login 경로에 대한 GET 요청을 수행합니다.
			.andExpect(status().isOk()); // 인증 없이 접근이 허용되므로 200 OK 응답을 기대합니다.
	}

	/**
	 * 인증되지 않은 사용자가 접근할 수 없는 보호된 리소스를 테스트합니다.
	 *
	 * @throws Exception 테스트 중 발생할 수 있는 예외
	 */
	@Test
	public void whenNotAuthenticated_thenUnauthorized() throws Exception {
		mockMvc.perform(get("/api/private")) // 보호된 API에 대한 GET 요청을 수행합니다.
			.andExpect(status().isUnauthorized()); // 인증되지 않은 사용자는 401 응답을 기대합니다.
	}

	/**
	 * 인증된 사용자가 접근할 수 있는 경로를 테스트합니다.
	 *
	 * @throws Exception 테스트 중 발생할 수 있는 예외
	 */
	@Test
	@WithMockUser // 인증된 사용자인 것처럼 모의 사용자를 설정합니다.
	public void whenAuthenticated_thenAccessible() throws Exception {
		mockMvc.perform(get("/swagger-ui.html")) // Swagger UI 페이지에 대한 GET 요청을 수행합니다.
			.andExpect(status().isOk()); // Swagger UI는 인증 없이 접근 가능하므로 200 OK 응답을 기대합니다.
	}

	/**
	 * 인증된 사용자가 접근할 수 없는 경로를 테스트합니다.
	 *
	 * @throws Exception 테스트 중 발생할 수 있는 예외
	 */
	@Test
	@WithMockUser // 인증된 사용자인 것처럼 모의 사용자를 설정합니다.
	public void whenAuthenticated_thenAccessDenied() throws Exception {
		mockMvc.perform(get("/api/protected")) // 보호된 API에 대한 GET 요청을 수행합니다.
			.andExpect(status().isForbidden()); // 권한이 부족한 경우 403 응답을 기대합니다.
	}
}