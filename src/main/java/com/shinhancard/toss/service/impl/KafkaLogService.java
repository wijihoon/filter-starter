package com.shinhancard.toss.service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.exception.KafkaException;
import com.shinhancard.toss.properties.KafkaProperties;
import com.shinhancard.toss.service.LogService;

import lombok.RequiredArgsConstructor;

/**
 * Kafka를 사용하여 로그를 전송하는 서비스 구현체입니다.
 * 이 클래스는 Kafka를 통해 로그 데이터를 전송합니다.
 */
@Service
@RequiredArgsConstructor
public class KafkaLogService implements LogService {

	private final KafkaTemplate<String, String> kafkaTemplate; // Kafka 템플릿 주입
	private final KafkaProperties kafkaProperties; // Kafka 프로퍼티 주입

	/**
	 * 로그 데이터를 Kafka를 통해 전송합니다.
	 *
	 * @param logJson 로그 데이터를 JSON 형식으로 변환한 문자열
	 * @throws KafkaException 로그 전송 중 문제가 발생한 경우
	 */
	@Override
	public void sendLog(String logJson) {
		try {
			// Kafka 프로퍼티에서 로그 전송용 토픽 이름 가져오기
			String topic = kafkaProperties.getProducer().getTopic().getLog();
			// Kafka 주제에 로그 데이터 전송
			kafkaTemplate.send(topic, logJson).get();
		} catch (Exception e) {
			// 로그 전송 중 문제가 발생하면 KafkaException 예외를 던짐
			throw new KafkaException(ErrorCode.KAFKA_SEND_FAILED);
		}
	}
}
