package com.shinhancard.toss;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * {@link TossApplication} 애플리케이션의 통합 테스트 클래스입니다.
 * <p>
 * 이 클래스는 애플리케이션의 전체 컨텍스트를 로드하고, HTTP 요청을 모의하여
 * 보안 설정과 CORS 설정을 검증합니다.
 * </p>
 */
@SpringBootTest
public class TossApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	/**
	 * 테스트 실행 전 MockMvc 인스턴스를 초기화합니다.
	 * <p>
	 * 이 메서드는 Spring Security 설정을 포함하여 MockMvc를 설정합니다.
	 * </p>
	 */
	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();
	}

	/**
	 * 인증 없이 접근할 수 있는 공개 엔드포인트를 테스트합니다.
	 * <p>
	 * /api/login 엔드포인트는 인증 없이 접근 가능해야 하며,
	 * 해당 경로에 대한 POST 요청이 성공적으로 응답해야 함을 검증합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	public void testPublicEndpoint() throws Exception {
		mockMvc.perform(post("/api/login"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists()); // 응답에 토큰이 포함되어야 함
	}

	/**
	 * 인증된 사용자가 보호된 엔드포인트에 접근할 수 있는지 테스트합니다.
	 * <p>
	 * 인증된 사용자로 보호된 엔드포인트에 접근할 수 있는지 확인합니다.
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 * @WithMockUser 어노테이션을 사용하여 인증된 사용자로 시뮬레이션합니다.
	 * </p>
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testProtectedEndpointWithAuthentication() throws Exception {
		mockMvc.perform(get("/api/protected"))
			.andExpect(status().isOk());
	}

	/**
	 * 인증 없이 보호된 엔드포인트에 접근할 경우 인증 실패 응답을 테스트합니다.
	 * <p>
	 * 인증 없이 보호된 엔드포인트에 접근할 경우 인증 실패 상태 코드(401)를 반환해야 함을 검증합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	public void testProtectedEndpointWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/protected"))
			.andExpect(status().isUnauthorized());
	}

	/**
	 * CORS 요청에 대한 응답 헤더가 올바르게 설정되는지 테스트합니다.
	 * <p>
	 * CORS 설정이 올바르게 적용되어 `Access-Control-Allow-Origin` 헤더가 설정되는지 확인합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	public void testCorsHeaders() throws Exception {
		mockMvc.perform(post("/api/login")
				.header("Origin", "http://allowed-origin.com"))
			.andExpect(header().string("Access-Control-Allow-Origin", "http://allowed-origin.com"));
	}

	/**
	 * 잘못된 HTTP 메서드에 대한 응답을 검증합니다.
	 * <p>
	 * 지원하지 않는 HTTP 메서드(GET)로 엔드포인트에 접근할 경우
	 * 적절한 오류 코드(405)를 반환해야 함을 검증합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testUnsupportedHttpMethod() throws Exception {
		mockMvc.perform(put("/api/protected"))
			.andExpect(status().isMethodNotAllowed()); // 메서드 불가 응답 검증
	}

	/**
	 * 잘못된 요청 파라미터를 처리하는지 테스트합니다.
	 * <p>
	 * 잘못된 요청 파라미터를 가진 POST 요청이 적절한 오류 응답을 반환해야 함을 검증합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	public void testInvalidRequestParameters() throws Exception {
		mockMvc.perform(post("/api/login")
				.param("invalidParam", "value"))
			.andExpect(status().isBadRequest()) // 잘못된 요청 응답 검증
			.andExpect(jsonPath("$.error").value("Invalid parameter")); // 오류 메시지 검증
	}

	/**
	 * 비밀번호가 일치하지 않는 경우 로그인 실패를 검증합니다.
	 * <p>
	 * 잘못된 비밀번호로 로그인 시도 시 적절한 실패 응답이 반환되어야 함을 검증합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	public void testLoginWithInvalidPassword() throws Exception {
		mockMvc.perform(post("/api/login")
				.param("username", "user")
				.param("password", "wrongPassword"))
			.andExpect(status().isUnauthorized()) // 인증 실패 응답 검증
			.andExpect(jsonPath("$.error").value("Invalid username or password")); // 오류 메시지 검증
	}

	/**
	 * 등록되지 않은 사용자로 로그인 시도 시 실패를 검증합니다.
	 * <p>
	 * 등록되지 않은 사용자로 로그인 시도 시 적절한 실패 응답이 반환되어야 함을 검증합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	public void testLoginWithUnregisteredUser() throws Exception {
		mockMvc.perform(post("/api/login")
				.param("username", "unregisteredUser")
				.param("password", "anyPassword"))
			.andExpect(status().isUnauthorized()) // 인증 실패 응답 검증
			.andExpect(jsonPath("$.error").value("Invalid username or password")); // 오류 메시지 검증
	}

	/**
	 * 사용자 권한에 따른 접근 제어를 검증합니다.
	 * <p>
	 * 관리자가 아닌 사용자가 관리자 전용 엔드포인트에 접근 시 접근 금지 응답을 검증합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testAdminEndpointAccessDenied() throws Exception {
		mockMvc.perform(get("/api/admin"))
			.andExpect(status().isForbidden()); // 접근 금지 응답 검증
	}

	/**
	 * 유효한 토큰을 사용한 요청에 대한 응답을 검증합니다.
	 * <p>
	 * 유효한 토큰을 포함한 요청이 성공적으로 응답해야 함을 검증합니다.
	 * </p>
	 *
	 * @throws Exception 테스트 실행 중 예외 발생 시
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testValidTokenUsage() throws Exception {
		mockMvc.perform(get("/api/protected")
				.header("Authorization", "Bearer validToken"))
			.andExpect(status().isOk()); // 정상 응답 검증
	}
}