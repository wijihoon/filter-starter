package com.shinhancard.toss.properties;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

/**
 * XSS 필터 설정을 프로파일별로 정의하는 클래스입니다.
 * <p>
 * 설정은 application.yml 파일의 xss 프로파일에 매핑됩니다.
 * 이 클래스는 XSS (Cross-Site Scripting) 공격을 탐지하기 위한 패턴을 구성합니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "xss")
@Validated
@Getter
@Setter
public class XssProperties {

	/**
	 * XSS 공격을 탐지할 패턴 리스트입니다.
	 * <p>
	 * 기본값은 비어 있는 리스트로 설정되어 있습니다. 설정 파일에서 값을 제공해야 합니다.
	 * </p>
	 */
	private List<String> patterns = Collections.emptyList();

	/**
	 * 패턴 검증 활성화 여부입니다.
	 * <p>
	 * 기본값은 enable 설정되어 있으며, 검증을 수행하지않는디.
	 * </p>
	 */
	private boolean enable = false;

	/**
	 * 설정된 패턴들을 검증합니다.
	 * <p>
	 * 패턴 리스트가 비어 있는 경우, 예외를 발생시킵니다. enableValidation이 true일 때만 검증을 수행합니다.
	 * </p>
	 */
	@PostConstruct
	private void validateProperties() {
		if (enable) {
			if (patterns.isEmpty()) {
				throw new IllegalArgumentException("At least one XSS pattern must be specified.");
			}
		}
	}

	/**
	 * 설정된 패턴들을 컴파일하여 Pattern 객체로 변환합니다.
	 * <p>
	 * 패턴이 비어 있지 않은 경우, 각 패턴을 컴파일하여 Pattern 객체로 변환합니다.
	 * </p>
	 * @return 컴파일된 XSS 패턴
	 */
	public Pattern getCompiledPattern() {
		StringBuilder patternBuilder = new StringBuilder();
		for (String pattern : patterns) {
			if (!patternBuilder.isEmpty()) {
				patternBuilder.append("|");
			}
			patternBuilder.append(pattern);
		}
		return Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
	}
}