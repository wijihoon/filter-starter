package com.shinhancard.toss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Toss 애플리케이션의 진입점을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 Spring Boot 애플리케이션을 시작하고 자동 구성을 설정합니다.
 * </p>
 */
@SpringBootApplication
public class TossApplication {

	/**
	 * 애플리케이션의 메인 메서드입니다.
	 * <p>
	 * 애플리케이션을 시작하는 진입점으로, SpringApplication.run 메서드를 호출하여
	 * Spring Boot 애플리케이션을 실행합니다.
	 * </p>
	 *
	 * @param args 커맨드라인 인수
	 */
	public static void main(String[] args) {
		// SpringApplication.run을 호출하여 TossApplication 클래스를 실행합니다.
		// 이 메서드는 Spring Boot 애플리케이션을 초기화하고 실행합니다.
		SpringApplication.run(TossApplication.class, args);
	}
}
