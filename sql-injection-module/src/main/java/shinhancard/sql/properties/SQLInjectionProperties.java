package shinhancard.sql.properties;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

/**
 * SQL 인젝션 필터 설정을 프로파일별로 정의하는 클래스입니다.
 * <p>
 * 설정은 application.yml 파일의 sql 프로파일에 매핑됩니다.
 * 이 클래스는 SQL 인젝션 공격을 탐지하기 위한 패턴을 구성합니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "filter.sql")
@Getter
@Setter
public class SQLInjectionProperties {

	/**
	 * SQL 인젝션 공격을 탐지할 패턴 리스트입니다.
	 * <p>
	 * 기본값은 SQL 인젝션 공격 시도를 탐지할 수 있는 다양한 패턴을 포함하고 있습니다.
	 * </p>
	 */
	private List<String> patterns = List.of(
		"([';]+|(--))", // SQL 주석 및 주입
		"(union.*select|select.*from|insert.*into|update.*set|delete.*from)", // 주요 SQL 명령어 패턴
		"(select.*from.*where.*\\d+\\s*or\\s*\\d+.*=\\d+)", // 숫자 기반 SQL 인젝션
		"(select.*from.*where.*\\d+\\s*and\\s*1=1)", // 1=1 SQL 인젝션
		"(sleep\\s*\\(\\d+\\))", // 시간 기반 SQL 인젝션
		"(benchmark\\s*\\(\\d+,\\s*md5\\(\\d+\\)\\))", // SQL 인젝션의 벤치마크 함수
		"('(?:[^']|\\\\')*'\\s*--)", // 문자열 종료 및 SQL 주석
		"(@@version|@@global.sql_mode|@@hostname|@@user)", // SQL 서버 설정 변수
		"('(?:[^']|\\\\')*'\\s*or\\s*'[^']*'\\s*='[^']*)", // OR 기반 SQL 인젝션
		"(information_schema.tables|information_schema.columns)", // 정보 스키마 테이블 탐색
		"(admin|root|user|select|drop|truncate|create|alter|exec|grant|revoke|union|order|by|--|;|#|/*|*/)"
	);

	/**
	 * 설정된 패턴들을 검증합니다.
	 * <p>
	 * 패턴 리스트가 비어 있는 경우, 예외를 발생시킵니다.
	 * </p>
	 */
	@PostConstruct
	private void validateProperties() {
		if (patterns == null || patterns.isEmpty()) {
			throw new IllegalArgumentException("At least one SQL injection pattern must be specified.");
		}
	}

	/**
	 * 설정된 패턴들을 컴파일하여 Pattern 객체로 변환합니다.
	 * <p>
	 * 패턴이 비어 있지 않은 경우, 각 패턴을 컴파일하여 Pattern 객체로 변환합니다.
	 * </p>
	 *
	 * @return 컴파일된 SQL 인젝션 패턴
	 */
	public Pattern getCompiledPattern() {
		if (patterns.isEmpty()) {
			return Pattern.compile(""); // 빈 문자열 패턴
		}

		// 빈 패턴을 제거하고 조합
		String combinedPattern = patterns.stream()
			.filter(pattern -> !pattern.trim().isEmpty()) // 빈 패턴 필터링
			.distinct()
			.collect(Collectors.joining("|"));

		return Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);
	}
}
