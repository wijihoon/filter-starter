package shinhancard.log.service.impl;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shinhancard.log.properties.KafkaProperties;
import shinhancard.log.service.LogService;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * KafkaProducer를 통해 로그를 Kafka에 전송하는 서비스입니다.
 * <p>
 * 이 서비스는 KafkaProducer를 사용하여 Kafka 토픽에 로그 메시지를 전송합니다.
 * </p>
 */
@Service
public class KafkaService implements LogService {

    private final KafkaProperties kafkaProperties;
    private final KafkaProducer<String, String> producer;

    /**
     * KafkaService의 생성자입니다.
     * <p>
     * KafkaProducer를 초기화하고 KafkaProperties를 주입받습니다.
     * </p>
     *
     * @param kafkaProperties Kafka 설정 속성
     */
    @Autowired
    public KafkaService(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getValueSerializer());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getAcks()); // acks 설정
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProperties.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProperties.getLingerMs());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProperties.getCompressionType());

        this.producer = new KafkaProducer<>(props);
    }

    /**
     * 로그 메시지를 Kafka에 전송합니다.
     * <p>
     * 로그 메시지를 Kafka 토픽에 비동기적으로 전송하며, acks 설정에 따라 응답을 처리합니다.
     * </p>
     *
     * @param logJson 전송할 로그 메시지 (JSON 형식)
     * @throws IOException 전송 중 I/O 오류가 발생한 경우
     */
    @Override
    public void sendLog(String logJson) throws IOException {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(kafkaProperties.getTopicName(), logJson);
            Future<?> future = producer.send(record);

            // acks 설정에 따라 전송 결과를 처리합니다.
            if ("1".equals(kafkaProperties.getAcks()) || "all".equals(kafkaProperties.getAcks())) {
                // 동기적으로 전송 결과를 기다립니다.
                future.get();
            }
            // "0" 설정일 경우 비동기적으로 결과를 기다리지 않음.

        } catch (Exception e) {
            throw new IOException("로그 전송 중 오류 발생", e);
        }
    }

    /**
     * KafkaProducer를 종료합니다.
     * <p>
     * 애플리케이션 종료 시 호출하여 리소스를 정리합니다.
     * </p>
     */
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}
