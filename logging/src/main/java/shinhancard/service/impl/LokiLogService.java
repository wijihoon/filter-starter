package shinhancard.service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shinhancard.properties.KafkaProperties;
import shinhancard.service.LogService;

/**
 * Kafka를 사용하여 로그를 전송하는 서비스 구현체입니다.
 * 이 클래스는 Kafka를 통해 로그 데이터를 전송합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LokiLogService implements LogService {

	private final KafkaTemplate<String, String> kafkaTemplate; // Kafka 템플릿 주입
	private final KafkaProperties kafkaProperties; // Kafka 프로퍼티 주입

	/**
	 * 로그 데이터를 Kafka를 통해 전송합니다.
	 *
	 * @param logJson 로그 데이터를 JSON 형식으로 변환한 문자열
	 */
	@Override
	public void sendLog(String logJson) {

		if (!kafkaProperties.isEnabled()) {
			// Kafka가 비활성화된 경우 로깅 처리
			log.info("Kafka logging is disabled. Log data: {}", logJson);
			return; // 로그 전송을 하지 않음
		}

		try {
			// Kafka 프로퍼티에서 로그 전송용 토픽 이름 가져오기
			String topic = kafkaProperties.getProducer().getTopic().getLog();
			// Kafka 주제에 로그 데이터 전송
			kafkaTemplate.send(topic, logJson).get();
		} catch (Exception e) {
			// 로그 전송 중 문제가 발생하면 KafkaException 예외를 던짐
			log.error("Failed to send log data to Kafka", e);
		}
	}
}