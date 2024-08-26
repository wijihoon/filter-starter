package shinhancard.xss.properties;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
@ConfigurationProperties(prefix = "filter.xss")
@Getter
@Setter
public class XSSProperties {

	/**
	 * XSS 공격을 탐지할 패턴 리스트입니다.
	 * <p>
	 * 기본값은 비어 있는 리스트로 설정되어 있습니다. 설정 파일에서 값을 제공해야 합니다.
	 * </p>
	 */
	private List<String> patterns = List.of(
		"<script.*?>.*?</script>", // <script> 태그
		"<.*?javascript:.*?>", // javascript 프로토콜
		"<.*?vbscript:.*?>", // vbscript 프로토콜
		"<.*?data:.*?>", // data URIs
		"<.*?expression\\(.*?>", // expressions
		"<.*?onload\\s*=.*?>", // onload 이벤트 핸들러
		"<.*?onclick\\s*=.*?>", // onclick 이벤트 핸들러
		"<.*?onerror\\s*=.*?>", // onerror 이벤트 핸들러
		"<.*?onmouseover\\s*=.*?>", // onmouseover 이벤트 핸들러
		"<.*?onfocus\\s*=.*?>", // onfocus 이벤트 핸들러
		"<.*?onchange\\s*=.*?>", // onchange 이벤트 핸들러
		"<.*?oninput\\s*=.*?>", // oninput 이벤트 핸들러
		"<.*?onabort\\s*=.*?>", // onabort 이벤트 핸들러
		"<.*?onbeforeunload\\s*=.*?>", // onbeforeunload 이벤트 핸들러
		"<.*?src\\s*=.*?>", // src 속성
		"<.*?href\\s*=.*?>", // href 속성
		"<.*?background\\s*=.*?>", // background 속성
		"<iframe.*?>.*?</iframe>", // iframe 태그
		"<object.*?>.*?</object>", // object 태그
		"<embed.*?>", // embed 태그
		"<form.*?>", // form 태그
		"<style.*?>", // style 태그
		"<.*?data-.*?>" // data 속성
	);

	/**
	 * 설정된 패턴들을 검증합니다.
	 * <p>
	 * 패턴 리스트가 비어 있는 경우, 예외를 발생시킵니다. enable이 true일 때만 검증을 수행합니다.
	 * </p>
	 */
	@PostConstruct
	private void validateProperties() {
		if (patterns == null || patterns.isEmpty()) {
			throw new IllegalArgumentException("At least one XSS pattern must be specified.");
		}
	}

	/**
	 * 설정된 패턴들을 컴파일하여 Pattern 객체로 변환합니다.
	 * <p>
	 * 패턴이 비어 있지 않은 경우, 각 패턴을 컴파일하여 Pattern 객체로 변환합니다.
	 * </p>
	 *
	 * @return 컴파일된 XSS 패턴
	 */
	public Pattern getCompiledPattern() {
		if (patterns.isEmpty()) {
			// 기본 패턴이 비어 있는 경우, 빈 패턴을 반환
			return Pattern.compile("^(?!)$"); // 항상 false를 반환하는 빈 패턴
		}

		// 중복 패턴 제거 및 패턴 병합
		String combinedPattern = patterns.stream()
			.distinct() // 중복 제거
			.collect(Collectors.joining("|")); // '|'로 패턴 연결

		return Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);
	}
}
