package shinhancard.logging.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Kafka 설정을 담고 있는 객체입니다.
 */
@Configuration
@ConfigurationProperties(prefix = "kafka")
@Validated
@Getter
@Setter
public class KafkaProperties {

    /**
     * Kafka 서버의 주소 목록 (예: localhost:9092)
     */
    @NotNull(message = "Kafka bootstrap servers는 null일 수 없습니다.")
    private String bootstrapServers;

    /**
     * Kafka 주제 (토픽) 이름
     */
    @NotNull(message = "Kafka topic name은 null일 수 없습니다.")
    private String topicName;

    /**
     * 메시지 전송 성공 응답을 기다리는 설정 (기본값: "1")
     * "all" - 모든 복제본에서 성공 응답을 기다립니다.
     * "1" - 리더에서만 성공 응답을 기다립니다.
     * "0" - 성공 응답을 기다리지 않습니다.
     */
    private String acks = "1";

    /**
     * 메시지 전송 실패 시 재시도 횟수 (기본값: 0)
     */
    private Integer retries = 0;

    /**
     * 메시지 배치 크기 (기본값: 16384)
     * 바이트 단위입니다.
     */
    private Integer batchSize = 16384;

    /**
     * 메시지 전송 지연 시간 (기본값: 0)
     * 밀리초 단위입니다.
     */
    private Integer lingerMs = 0;

    /**
     * 메시지 압축 타입 (기본값: none)
     * "none" - 압축 없음
     * "gzip" - GZIP 압축
     * "snappy" - Snappy 압축
     * "lz4" - LZ4 압축
     */
    private String compressionType = "none";

    /**
     * 메시지 키와 값을 직렬화하기 위한 설정
     */
    private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
}
