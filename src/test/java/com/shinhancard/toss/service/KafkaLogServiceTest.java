package com.shinhancard.toss.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import com.shinhancard.toss.exception.KafkaException;
import com.shinhancard.toss.properties.KafkaProperties;
import com.shinhancard.toss.service.impl.KafkaLogService;

/**
 * KafkaLogService의 기능을 테스트하기 위한 단위 테스트 클래스입니다.
 */
class KafkaLogServiceTest {

	@Mock
	private KafkaTemplate<String, String> kafkaTemplate; // KafkaTemplate Mock 객체

	@Mock
	private KafkaProperties kafkaProperties; // KafkaProperties Mock 객체

	@InjectMocks
	private KafkaLogService kafkaLogService; // 테스트할 KafkaLogService 객체

	/**
	 * 테스트 실행 전에 Mock 객체를 초기화합니다.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Mockito의 Mock 객체를 초기화
	}

	/**
	 * 로그 데이터를 Kafka에 성공적으로 전송하는 테스트
	 */
	@Test
	@DisplayName("로그 데이터를 Kafka에 성공적으로 전송하는 테스트")
	void testSendLogSuccess() throws Exception {
		String logJson = "{\"message\": \"test log\"}"; // 전송할 로그 데이터

		// KafkaProperties의 ProducerProperties와 TopicProperties Mock 설정
		KafkaProperties.Producer.Topic topicProperties = mock(KafkaProperties.Producer.Topic.class);
		when(topicProperties.getLog()).thenReturn("logTopic"); // 토픽 이름 설정

		KafkaProperties.Producer producerProperties = mock(KafkaProperties.Producer.class);
		when(producerProperties.getTopic()).thenReturn(topicProperties); // 프로퍼티 설정

		when(kafkaProperties.getProducer()).thenReturn(producerProperties); // KafkaProperties 설정

		// KafkaTemplate의 send 메서드가 성공적으로 호출되도록 설정
		when(kafkaTemplate.send("logTopic", logJson)).thenReturn(null);

		// 로그 전송 메서드 호출
		kafkaLogService.sendLog(logJson);

		// send 메서드가 정상적으로 호출되었는지 검증
		verify(kafkaTemplate).send("logTopic", logJson);
	}

	/**
	 * 로그 데이터를 Kafka에 전송할 때 예외가 발생하는 경우를 테스트합니다.
	 */
	@Test
	@DisplayName("로그 데이터를 Kafka에 전송할 때 예외가 발생하는 경우를 테스트합니다.")
	void testSendLogFailure() throws Exception {
		String logJson = "{\"message\": \"test log\"}"; // 전송할 로그 데이터

		// KafkaProperties의 ProducerProperties와 TopicProperties Mock 설정
		KafkaProperties.Producer.Topic topicProperties = mock(KafkaProperties.Producer.Topic.class);
		when(topicProperties.getLog()).thenReturn("logTopic"); // 토픽 이름 설정

		KafkaProperties.Producer producerProperties = mock(KafkaProperties.Producer.class);
		when(producerProperties.getTopic()).thenReturn(topicProperties); // 프로퍼티 설정

		when(kafkaProperties.getProducer()).thenReturn(producerProperties); // KafkaProperties 설정

		// KafkaTemplate의 send 메서드가 예외를 던지도록 설정
		when(kafkaTemplate.send("logTopic", logJson)).thenThrow(new RuntimeException("Send failed"));

		// 로그 전송 메서드 호출 시 KafkaException이 발생하는지 검증
		assertThrows(KafkaException.class, () -> kafkaLogService.sendLog(logJson));
	}
}