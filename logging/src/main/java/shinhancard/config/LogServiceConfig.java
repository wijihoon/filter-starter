package shinhancard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;

import shinhancard.properties.KafkaProperties;
import shinhancard.service.LogService;
import shinhancard.service.impl.KafkaLogService;
import shinhancard.service.impl.LokiLogService;

@Configuration
public class LogServiceConfig {
	@Bean
	@ConditionalOnProperty(name = "logging.type", havingValue = "kafka", matchIfMissing = true)
	public LogService kafkaLogService(KafkaTemplate<String, String> kafkaTemplate, KafkaProperties kafkaProperties) {
		return new KafkaLogService(kafkaTemplate, kafkaProperties);
	}

	@Bean
	@ConditionalOnProperty(name = "logging.type", havingValue = "loki")
	public LogService lokiLogService(KafkaTemplate<String, String> kafkaTemplate, KafkaProperties kafkaProperties) {
		return new LokiLogService(kafkaTemplate, kafkaProperties);
	}
}

