package shinhancard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shinhancard.properties.LogProperties;
import shinhancard.service.LogService;
import shinhancard.service.impl.KafkaService;
import shinhancard.service.impl.LokiService;

/**
 * 로그 설정을 기반으로 {@link LogService} 빈을 설정하는 클래스입니다.
 * <p>
 * 이 클래스는 {@link LogProperties} 설정에 따라 적절한 로그 서비스 구현체를 반환합니다.
 * </p>
 */
@Configuration
public class LogConfig {

    private final LogProperties logProperties;
    private final KafkaService kafkaService;
    private final LokiService lokiService;

    /**
     * 생성자입니다. {@link LogProperties}, {@link KafkaService}, {@link LokiService}를 주입받습니다.
     *
     * @param logProperties 로그 관련 설정을 담고 있는 {@link LogProperties} 객체
     * @param kafkaService  Kafka 로그 서비스를 제공하는 {@link KafkaService} 객체
     * @param lokiService   Loki 로그 서비스를 제공하는 {@link LokiService} 객체
     */
    public LogConfig(LogProperties logProperties, KafkaService kafkaService, LokiService lokiService) {
        this.logProperties = logProperties;
        this.kafkaService = kafkaService;
        this.lokiService = lokiService;
    }

    /**
     * {@link LogProperties}의 설정을 기반으로 적절한 {@link LogService}를 반환합니다.
     * <p>
     * {@link LogProperties#getLogDestination()} 메서드를 통해 로그 전송 방식을 확인하고,
     * 해당 방식에 따라 {@link KafkaService} 또는 {@link LokiService} 객체를 반환합니다.
     * </p>
     *
     * @return 선택된 {@link LogService} 객체
     * @throws IllegalArgumentException 지원하지 않는 로그 전송 방식이 설정된 경우
     */
    @Bean
    public LogService logService() {
        String logDestination = logProperties.getLogDestination(); // LogProperties에서 로그 전송 방식 가져오기
        return switch (logDestination.toLowerCase()) {
            case "kafka" -> kafkaService;
            case "loki" -> lokiService;
            default -> throw new IllegalArgumentException("지원하지 않는 로그 전송 방식: " + logDestination);
        };
    }
}
