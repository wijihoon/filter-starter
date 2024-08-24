package shinhancard.sql.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * 기본값은 비어 있는 리스트로 설정되어 있습니다. 설정 파일에서 값을 제공해야 합니다.
     * </p>
     */
    private List<String> patterns = List.of(
            ".*([';]+|(--)).*", // SQL 주석 및 SQL 주입
            ".*union.*select.*", // UNION SELECT
            ".*select.*from.*", // SELECT FROM
            ".*insert.*into.*", // INSERT INTO
            ".*update.*set.*", // UPDATE SET
            ".*delete.*from.*" // DELETE FROM
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
            return Pattern.compile("^(?!)$"); // 항상 false를 반환하는 빈 패턴
        }

        String combinedPattern = patterns.stream()
                .distinct()
                .collect(Collectors.joining("|"));

        return Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);
    }
}
