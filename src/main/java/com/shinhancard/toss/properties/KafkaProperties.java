package com.shinhancard.toss.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Kafka 설정을 위한 프로퍼티 클래스입니다.
 * <p>
 * 이 클래스는 application.yml 또는 application.properties 파일에서 Kafka 관련 설정을 주입받아 처리합니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "kafka") // application.yml의 "kafka" 접두사에 매핑된 프로퍼티를 읽어옵니다.
@Validated // 유효성 검사를 활성화합니다.
@Getter
@Setter
public class KafkaProperties {

	/**
	 * Kafka 브로커의 주소를 설정합니다.
	 */
	@NotNull(message = "Bootstrap servers cannot be null")
	private String bootstrapServers;

	/**
	 * Kafka 프로듀서 설정을 담고 있는 객체입니다.
	 */
	private Producer producer = new Producer();

	@PostConstruct
	private void validateProperties() {
		if (bootstrapServers == null || bootstrapServers.isEmpty()) {
			throw new IllegalArgumentException("Bootstrap servers cannot be empty.");
		}
	}

	@Getter
	@Setter
	public static class Producer {
		/**
		 * 프로듀서의 key 직렬화 클래스입니다.
		 */
		private String keySerializer;

		/**
		 * 프로듀서의 value 직렬화 클래스입니다.
		 */
		private String valueSerializer;

		/**
		 * Kafka 토픽 설정을 담고 있는 객체입니다.
		 */
		private Topic topic = new Topic();

		@Getter
		@Setter
		public static class Topic {
			/**
			 * 로그 데이터를 전송할 Kafka 토픽입니다.
			 */
			@NotNull(message = "Log topic cannot be null")
			private String log;
		}
	}
}
