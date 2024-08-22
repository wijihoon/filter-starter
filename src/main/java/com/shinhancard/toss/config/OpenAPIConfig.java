package com.shinhancard.toss.config;

import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shinhancard.toss.properties.OpenAPIProperties;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

/**
 * OpenAPI 설정을 위한 구성 클래스입니다.
 * <p>
 * OpenAPI 스펙을 생성하고, Swagger UI에서 API 문서를 제공하기 위한 설정을 구성합니다.
 * </p>
 */
@Configuration // Spring의 설정 클래스를 나타냅니다.
@RequiredArgsConstructor
public class OpenAPIConfig {

	private final OpenAPIProperties openAPIProperties;

	/**
	 * OpenAPI 인스턴스를 생성하고, 설정값을 기반으로 구성합니다.
	 * <p>
	 * OpenAPI 인스턴스를 설정값에 맞게 구성하여 Swagger UI에서 API 문서를
	 * 제공할 수 있도록 설정합니다.
	 * </p>
	 *
	 * @return 구성된 OpenAPI 인스턴스
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		// OpenAPI 설정에서 Contact 정보를 가져와 설정합니다.
		Contact contact = new Contact()
			.name(openAPIProperties.getContact().getName())
			.url(openAPIProperties.getContact().getUrl())
			.email(openAPIProperties.getContact().getEmail());

		// OpenAPI 인스턴스를 생성하고 설정값을 기반으로 구성합니다.
		return new OpenAPI()
			.info(new Info()
				.title(openAPIProperties.getTitle())
				.version(openAPIProperties.getVersion())
				.description(openAPIProperties.getDescription())
				.contact(contact)) // Contact 정보를 설정합니다.
			.servers(openAPIProperties.getServers().stream()
				.map(server -> new Server() // 각 서버 정보를 Server 객체로 변환합니다.
					.url(server.getUrl())
					.description(server.getDescription())) // 서버 URL과 설명을 설정합니다.
				.collect(Collectors.toList())); // 서버 객체들을 리스트로 수집합니다.
	}
}