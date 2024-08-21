// package com.shinhancard.toss.properties;
//
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.validation.annotation.Validated;
//
// import jakarta.annotation.PostConstruct;
// import jakarta.validation.constraints.Min;
// import jakarta.validation.constraints.NotBlank;
// import lombok.Getter;
// import lombok.Setter;
//
// /**
//  * Redis 설정을 프로파일별로 정의하는 클래스입니다.
//  * <p>
//  * 이 클래스는 application.yml 파일의 "spring.redis" 프로파일에 매핑되어
//  * Redis 서버의 설정을 주입받아 처리합니다.
//  * </p>
//  */
// @Configuration
// @ConfigurationProperties(prefix = "redis")
// @Getter
// @Setter
// @Validated // 유효성 검사를 활성화합니다.
// public class RedisProperties {
//
// 	/**
// 	 * Redis 서버의 호스트명입니다.
// 	 * <p>
// 	 * 예: "localhost" 또는 "redis.example.com"
// 	 * </p>
// 	 */
// 	@NotBlank(message = "Redis host cannot be blank") // 빈 값이 허용되지 않습니다.
// 	private String host = "localhost"; // 기본값 설정
//
// 	/**
// 	 * Redis 서버의 포트 번호입니다.
// 	 * <p>
// 	 * Redis 서버의 기본 포트는 6379입니다. 포트 번호는 1024 이상이어야 합니다.
// 	 * </p>
// 	 */
// 	@Min(value = 1024, message = "Redis port must be at least 1024") // 포트 번호의 최소값 설정
// 	private int port = 6379; // 기본값 설정
//
// 	/**
// 	 * Redis 서버의 비밀번호입니다.
// 	 * <p>
// 	 * 비밀번호가 설정되지 않은 경우에는 null이 될 수 있습니다.
// 	 * </p>
// 	 */
// 	private String password;
//
// 	/**
// 	 * Redis 커넥션 타임아웃(밀리초 단위)입니다.
// 	 * <p>
// 	 * 타임아웃 값이 너무 작으면 네트워크 문제로 인해 커넥션 실패가 발생할 수 있습니다.
// 	 * 기본값은 2000ms입니다. 타임아웃 값은 1000ms 이상이어야 합니다.
// 	 * </p>
// 	 */
// 	@Min(value = 1000, message = "Redis timeout must be at least 1000 milliseconds") // 타임아웃의 최소값 설정
// 	private int timeout = 2000; // 기본값 설정
//
// 	/**
// 	 * 설정된 값들이 유효한지 검증합니다.
// 	 * <p>
// 	 * 빈이 초기화된 후에 호출되어 설정 값의 유효성을 검사합니다.
// 	 * </p>
// 	 */
// 	@PostConstruct
// 	private void validateProperties() {
// 		// 호스트가 null이거나 빈 값인 경우 예외를 발생시킵니다.
// 		if (host == null || host.trim().isEmpty()) {
// 			throw new IllegalArgumentException("Redis host cannot be null or empty");
// 		}
//
// 		// 포트 번호가 유효한 범위 (1024 - 65535) 내에 있는지 확인합니다.
// 		if (port < 1024 || port > 65535) {
// 			throw new IllegalArgumentException("Redis port must be between 1024 and 65535");
// 		}
//
// 		// 타임아웃 값이 1000ms 이상인지 확인합니다.
// 		if (timeout < 1000) {
// 			throw new IllegalArgumentException("Redis timeout must be at least 1000 milliseconds");
// 		}
// 	}
// }