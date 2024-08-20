package com.shinhancard.toss.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.shinhancard.toss.properties.OpenAPIProperties;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@SpringBootTest // Spring Boot 애플리케이션의 컨텍스트를 로드하여 테스트를 실행합니다.
public class OpenAPIConfigTest {

	/**
	 * OpenAPIConfig의 customOpenAPI 메소드가 올바르게 설정된 OpenAPI 인스턴스를 반환하는지 테스트합니다.
	 * <p>
	 * OpenAPIProperties를 Mocking하여 설정값을 제공하고, 반환된 OpenAPI 인스턴스의 설정값을 검증합니다.
	 * </p>
	 */
	@Test
	@DisplayName("OpenAPI 인스턴스가 올바르게 설정되는지 테스트")
	void customOpenAPIConfigurationTest() {
		// Given: OpenAPIProperties를 Mocking하여 설정값을 제공
		OpenAPIProperties mockOpenAPIProperties = Mockito.mock(OpenAPIProperties.class);
		when(mockOpenAPIProperties.getTitle()).thenReturn("API 제목");
		when(mockOpenAPIProperties.getVersion()).thenReturn("1.0.0");
		when(mockOpenAPIProperties.getDescription()).thenReturn("API 설명");

		// OpenAPIConfig 인스턴스를 생성
		OpenAPIConfig openAPIConfig = new OpenAPIConfig(mockOpenAPIProperties);

		// When: customOpenAPI 메소드를 호출하여 OpenAPI 인스턴스를 생성
		OpenAPI openAPI = openAPIConfig.customOpenAPI();

		// Then: OpenAPI 인스턴스가 올바르게 설정되었는지 검증
		assertNotNull(openAPI, "OpenAPI 인스턴스가 null입니다."); // OpenAPI 인스턴스가 null이 아님을 확인

		Info info = openAPI.getInfo();
		assertEquals("API 제목", info.getTitle(), "API 제목이 올바르지 않습니다."); // API 제목 검증
		assertEquals("1.0.0", info.getVersion(), "API 버전이 올바르지 않습니다."); // API 버전 검증
		assertEquals("API 설명", info.getDescription(), "API 설명이 올바르지 않습니다."); // API 설명 검증

		Contact contact = info.getContact();
		assertNotNull(contact, "Contact 정보가 null입니다."); // Contact 정보가 null이 아님을 확인
		assertEquals("John Doe", contact.getName(), "Contact 이름이 올바르지 않습니다."); // Contact 이름 검증
		assertEquals("http://example.com", contact.getUrl(), "Contact URL이 올바르지 않습니다."); // Contact URL 검증
		assertEquals("john.doe@example.com", contact.getEmail(), "Contact 이메일이 올바르지 않습니다."); // Contact 이메일 검증

		List<Server> servers = openAPI.getServers();
		assertNotNull(servers, "서버 리스트가 null입니다."); // 서버 리스트가 null이 아님을 확인
		assertEquals(1, servers.size(), "서버 리스트의 크기가 올바르지 않습니다."); // 서버 리스트 크기 검증

		Server server = servers.getFirst();
		assertEquals("http://localhost:8080", server.getUrl(), "서버 URL이 올바르지 않습니다."); // 서버 URL 검증
		assertEquals("로컬 서버", server.getDescription(), "서버 설명이 올바르지 않습니다."); // 서버 설명 검증
	}
}