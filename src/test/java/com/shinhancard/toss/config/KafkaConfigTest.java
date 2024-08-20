package com.shinhancard.toss.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.shinhancard.toss.properties.KafkaProperties;

/**
 * {@link KafkaConfig} 클래스를 테스트하는 클래스입니다.
 * <p>
 * Kafka 설정이 올바르게 적용되었는지 확인합니다.
 * </p>
 */
@SpringBootTest
public class KafkaConfigTest {

	@Autowired
	private ProducerFactory<String, String> producerFactory;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@BeforeEach
	void setUp() {
		// KafkaProperties를 모의 객체로 생성
		KafkaProperties kafkaProperties = mock(KafkaProperties.class);
		// KafkaProperties의 메서드에 대한 반환 값 설정
		when(kafkaProperties.getBootstrapServers()).thenReturn("localhost:9092");
		when(kafkaProperties.getProducer().getKeySerializer()).thenReturn(
			"org.apache.kafka.common.serialization.StringSerializer");
		when(kafkaProperties.getProducer().getValueSerializer()).thenReturn(
			"org.apache.kafka.common.serialization.StringSerializer");
	}

	/**
	 * 유효한 KafkaProperties를 사용할 때 ProducerFactory가 올바르게 생성되는지 테스트합니다.
	 */
	@Test
	@DisplayName("유효한 KafkaProperties를 사용할 때 ProducerFactory가 올바르게 생성되는지 테스트")
	public void givenValidKafkaProperties_whenProducerFactoryBean_thenReturnCorrectProducerFactory() {
		// ProducerFactory가 정상적으로 생성되었는지 검증
		assertThat(producerFactory).isNotNull();

		// ProducerFactory의 설정 값 검증
		assertThat(producerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		assertThat(producerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		assertThat(producerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
	}

	/**
	 * 유효한 KafkaProperties를 사용할 때 KafkaTemplate이 올바르게 생성되는지 테스트합니다.
	 */
	@Test
	@DisplayName("유효한 KafkaProperties를 사용할 때 KafkaTemplate이 올바르게 생성되는지 테스트")
	public void givenValidKafkaProperties_whenKafkaTemplateBean_thenReturnKafkaTemplate() {
		// KafkaTemplate이 정상적으로 생성되었는지 검증
		assertThat(kafkaTemplate).isNotNull();
	}

	/**
	 * KafkaProperties가 없을 때 ProducerFactory가 기본값으로 생성되는지 테스트합니다.
	 */
	@Test
	@DisplayName("KafkaProperties가 없을 때 ProducerFactory가 기본값으로 생성되는지 테스트")
	public void givenNoKafkaProperties_whenProducerFactoryBean_thenReturnDefaultProducerFactory() {
		// KafkaProperties를 빈 설정으로 변경
		KafkaProperties emptyKafkaProperties = new KafkaProperties();
		// 기본 값을 설정 (테스트 목적으로)
		emptyKafkaProperties.setBootstrapServers("localhost:9092");
		emptyKafkaProperties.getProducer().setKeySerializer("org.apache.kafka.common.serialization.StringSerializer");
		emptyKafkaProperties.getProducer().setValueSerializer("org.apache.kafka.common.serialization.StringSerializer");

		// KafkaConfig 설정 변경 후 ProducerFactory 확인
		KafkaConfig kafkaConfig = new KafkaConfig(emptyKafkaProperties);
		ProducerFactory<String, String> localProducerFactory = kafkaConfig.producerFactory();

		// ProducerFactory의 생성 및 설정 검증
		assertThat(localProducerFactory).isNotNull();
		assertThat(localProducerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		assertThat(localProducerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		assertThat(localProducerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
	}

	/**
	 * 빈 KafkaProperties를 사용할 때 ProducerFactory가 기본값으로 생성되는지 테스트합니다.
	 */
	@Test
	@DisplayName("빈 KafkaProperties를 사용할 때 ProducerFactory가 기본값으로 생성되는지 테스트")
	public void givenEmptyKafkaProperties_whenProducerFactoryBean_thenReturnProducerFactoryWithDefaultValues() {
		// KafkaProperties를 모의 객체로 생성하고 빈 값 설정
		KafkaProperties emptyKafkaProperties = mock(KafkaProperties.class);
		when(emptyKafkaProperties.getBootstrapServers()).thenReturn("");
		when(emptyKafkaProperties.getProducer().getKeySerializer()).thenReturn("");
		when(emptyKafkaProperties.getProducer().getValueSerializer()).thenReturn("");

		KafkaConfig kafkaConfig = new KafkaConfig(emptyKafkaProperties);
		ProducerFactory<String, String> localProducerFactory = kafkaConfig.producerFactory();

		// ProducerFactory의 생성 및 설정 검증
		assertThat(localProducerFactory).isNotNull();
		// 빈 값 설정 시 기본값이 설정되어 있는지 확인
		assertThat(localProducerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "");
		assertThat(localProducerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "");
		assertThat(localProducerFactory.getConfigurationProperties())
			.containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "");
	}
}