package com.shinhancard.toss.config;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.shinhancard.toss.properties.KafkaProperties;

import lombok.RequiredArgsConstructor;

/**
 * Kafka 프로듀서 설정을 위한 클래스입니다.
 * <p>
 * {@link KafkaProperties} 클래스로부터 Kafka 설정을 주입받아 Kafka 프로듀서를 설정합니다.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
	private final KafkaProperties kafkaProperties;

	/**
	 * Kafka 프로듀서 팩토리를 생성합니다.
	 * <p>
	 * {@link ProducerFactory}를 사용하여 Kafka 프로듀서의 기본 설정을 반환합니다.
	 * </p>
	 *
	 * @return Kafka 프로듀서 팩토리 객체
	 */
	@Bean
	public ProducerFactory<String, String> producerFactory() {
		// Kafka 프로듀서 설정을 위한 Map 생성
		return new DefaultKafkaProducerFactory<>(Map.of(
			ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(), // Kafka 서버 주소 설정
			ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getKeySerializer(), // 키 직렬화기 설정
			ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getValueSerializer()
			// 값 직렬화기 설정
		));
	}

	/**
	 * KafkaTemplate을 생성합니다.
	 * <p>
	 * {@link KafkaTemplate}을 사용하여 Kafka 프로듀서와의 상호작용을 위한 템플릿을 반환합니다.
	 * </p>
	 *
	 * @return KafkaTemplate 객체
	 */
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		// KafkaTemplate을 생성하여 반환
		return new KafkaTemplate<>(producerFactory());
	}
}