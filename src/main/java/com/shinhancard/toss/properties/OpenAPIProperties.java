package com.shinhancard.toss.properties;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * OpenAPI 설정을 관리하는 클래스입니다.
 * <p>
 * 이 클래스는 application.yml 또는 application.properties 파일에서
 * OpenAPI 관련 설정을 주입받아 처리합니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "openapi") // "openapi" 접두사로 설정을 읽어옵니다.
@Validated // 유효성 검사를 활성화합니다.
@Getter
@Setter
public class OpenAPIProperties {

	/**
	 * OpenAPI 문서의 제목입니다.
	 * <p>
	 * 기본값은 "Default API Title"입니다.
	 * </p>
	 */
	@NotBlank(message = "Title cannot be blank") // 빈 값이 허용되지 않습니다.
	private String title = "Default API Title";

	/**
	 * OpenAPI 문서의 버전입니다.
	 * <p>
	 * 기본값은 "1.0.0"입니다.
	 * </p>
	 */
	@NotBlank(message = "Version cannot be blank") // 빈 값이 허용되지 않습니다.
	private String version = "1.0.0";

	/**
	 * OpenAPI 문서의 설명입니다.
	 * <p>
	 * 최대 500자까지 허용됩니다. 기본값은 "This is a default description for the API."입니다.
	 * </p>
	 */
	@Size(max = 500, message = "Description should be at most 500 characters long") // 최대 500자까지 허용됩니다.
	private String description = "This is a default description for the API.";

	/**
	 * OpenAPI 문서의 연락처 정보입니다.
	 * <p>
	 * 이 필드는 null이 될 수 없으며 기본값이 설정되어 있습니다.
	 * </p>
	 */
	@NotNull(message = "Contact information cannot be null") // null 값이 허용되지 않습니다.
	private Contact contact = new Contact();

	/**
	 * OpenAPI 문서에서 사용하는 서버 리스트입니다.
	 * <p>
	 * 빈 리스트로 초기화됩니다.
	 * </p>
	 */
	private List<Server> servers = Collections.emptyList();

	/**
	 * 클래스 초기화 후 설정을 검증합니다.
	 * <p>
	 * 서버 URL이 빈 값인 경우 예외를 발생시킵니다.
	 * </p>
	 */
	@PostConstruct
	private void validateProperties() {
		// 서버 리스트가 null인 경우 빈 리스트로 초기화합니다.
		if (servers == null) {
			servers = Collections.emptyList();
		}
		// 각 서버의 URL이 빈 값인지 확인합니다.
		for (Server server : servers) {
			if (server.getUrl().isBlank()) {
				throw new IllegalArgumentException("Each server URL must be specified.");
			}
		}
	}

	/**
	 * OpenAPI 문서의 연락처 정보를 나타내는 내부 클래스입니다.
	 */
	@Getter
	@Setter
	public static class Contact {
		/**
		 * 연락처 이름입니다.
		 * <p>
		 * 기본값은 "Default Contact Name"입니다.
		 * </p>
		 */
		@NotBlank(message = "Contact name cannot be blank") // 빈 값이 허용되지 않습니다.
		private String name = "Default Contact Name";

		/**
		 * 연락처 URL입니다.
		 * <p>
		 * 기본값은 "<a href="http://default-contact-url.com">...</a>"입니다.
		 * </p>
		 */
		@NotBlank(message = "Contact URL cannot be blank") // 빈 값이 허용되지 않습니다.
		private String url = "http://default-contact-url.com";

		/**
		 * 연락처 이메일 주소입니다.
		 * <p>
		 * 기본값은 "contact@example.com"입니다.
		 * </p>
		 */
		@NotBlank(message = "Contact email cannot be blank") // 빈 값이 허용되지 않습니다.
		private String email = "contact@example.com";
	}

	/**
	 * OpenAPI 문서의 서버 정보를 나타내는 내부 클래스입니다.
	 */
	@Getter
	@Setter
	public static class Server {
		/**
		 * 서버 URL입니다.
		 * <p>
		 * 기본값은 "<a href="http://default-server-url.com">...</a>"입니다.
		 * </p>
		 */
		@NotBlank(message = "Server URL cannot be blank") // 빈 값이 허용되지 않습니다.
		private String url = "http://default-server-url.com";

		/**
		 * 서버 설명입니다.
		 * <p>
		 * 최대 100자까지 허용됩니다. 기본값은 "Default server description"입니다.
		 * </p>
		 */
		@Size(max = 100, message = "Server description should be at most 100 characters long") // 최대 100자까지 허용됩니다.
		private String description = "Default server description";
	}
}
