package shinhancard.logging.service.impl;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import shinhancard.logging.properties.KafkaProperties;
import shinhancard.logging.service.LogService;

/**
 * KafkaProducer를 통해 로그를 Kafka에 전송하는 서비스입니다.
 */
@Service
@Slf4j
public class KafkaService implements LogService {

	private final KafkaProperties kafkaProperties;
	private final KafkaProducer<String, String> producer;

	@Autowired
	public KafkaService(KafkaProperties kafkaProperties) {
		this.kafkaProperties = kafkaProperties;
		this.producer = createProducer();
	}

	private KafkaProducer<String, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getKeySerializer());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getValueSerializer());
		props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getAcks());
		props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getRetries());
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProperties.getBatchSize());
		props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProperties.getLingerMs());
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProperties.getCompressionType());

		return new KafkaProducer<>(props);
	}

	@Override
	public void sendLog(String logJson) throws IOException {
		ProducerRecord<String, String> kafkaRecord = new ProducerRecord<>(kafkaProperties.getTopicName(), logJson);
		try {
			Future<?> sendResultFuture = producer.send(kafkaRecord);

			if ("1".equals(kafkaProperties.getAcks()) || "all".equals(kafkaProperties.getAcks())) {
				sendResultFuture.get();
			}
		} catch (InterruptedException e) {
			// Restore interrupted status and handle exception
			Thread.currentThread().interrupt();
			throw new IOException("Log sending interrupted", e);
		} catch (ExecutionException e) {
			throw new IOException("Error while sending log", e);
		}
	}

	public void close() {
		try {
			if (producer != null) {
				producer.close();
			}
		} catch (Exception e) {
			log.error("Failed to close Kafka producer: " + e.getMessage());
		}
	}

}
