package shinhancard.logging.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import shinhancard.logging.filter.LogFilter;
import shinhancard.logging.properties.KafkaProperties;
import shinhancard.logging.properties.LogProperties;
import shinhancard.logging.properties.LokiProperties;
import shinhancard.logging.service.LogService;
import shinhancard.logging.service.impl.KafkaService;
import shinhancard.logging.service.impl.LokiService;

/**
 * 로그 관련 자동 구성을 제공하는 클래스입니다.
 * <p>
 * 이 클래스는 로그 필터를 자동으로 등록하며, 로그 전송 서비스를 설정합니다.
 * </p>
 */
@AutoConfiguration
@EnableConfigurationProperties({LogProperties.class, KafkaProperties.class, LokiProperties.class})
@ConditionalOnProperty(name = "filter.log.enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfiguration {

	private final LogProperties logProperties;
	private final KafkaProperties kafkaProperties;
	private final LokiProperties lokiProperties;

	/**
	 * LogAutoConfiguration의 생성자입니다.
	 * <p>
	 * LogProperties, KafkaProperties, LokiProperties를 주입받아 로그 필터와 로그 전송 서비스를 설정합니다.
	 * </p>
	 *
	 * @param logProperties 로그 설정
	 * @param kafkaProperties Kafka 설정
	 * @param lokiProperties Loki 설정
	 */
	public LogAutoConfiguration(LogProperties logProperties, KafkaProperties kafkaProperties,
		LokiProperties lokiProperties) {
		this.logProperties = logProperties;
		this.kafkaProperties = kafkaProperties;
		this.lokiProperties = lokiProperties;
	}

	/**
	 * 로그 전송 서비스 빈을 생성합니다.
	 * <p>
	 * LogProperties의 설정에 따라 적절한 LogService 구현체를 반환합니다.
	 * </p>
	 *
	 * @return LogService 구현체
	 */
	@Bean
	public LogService logService() {
		String logDestination = logProperties.getLogDestination();

		if ("kafka".equalsIgnoreCase(logDestination)) {
			return new KafkaService(kafkaProperties);
		} else if ("loki".equalsIgnoreCase(logDestination)) {
			return new LokiService(lokiProperties);
		} else {
			throw new IllegalArgumentException("지원되지 않는 로그 전송 방식: " + logDestination);
		}
	}

	/**
	 * LogFilter를 Spring 컨텍스트에 등록합니다.
	 * <p>
	 * FilterRegistrationBean을 사용하여 LogFilter를 필터 체인에 등록하고, 순서를 설정합니다.
	 * </p>
	 *
	 * @param logService 로그 전송 서비스
	 * @return FilterRegistrationBean 객체
	 */
	@Bean
	public FilterRegistrationBean<LogFilter> logFilterRegistration(LogService logService) {
		FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new LogFilter(logProperties, logService));
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 필터의 순서를 설정 (최우선순위)
		return registrationBean;
	}
}
