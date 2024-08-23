package shinhancard.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 로그 설정을 담고 있는 객체입니다.
 * <p>
 * 이 클래스는 애플리케이션의 로그 설정을 정의하고, 기본값을 설정합니다.
 * 설정은 외부 구성 파일에서 읽어옵니다.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "log")
@Getter
@Setter
public class LogProperties {

    /**
     * 요청 본문에 대한 설정입니다.
     */
    private RequestBody requestBody = new RequestBody();

    /**
     * 응답 본문에 대한 설정입니다.
     */
    private ResponseBody responseBody = new ResponseBody();

    /**
     * 로그 본문에 대한 설정입니다.
     */
    @NotNull(message = "본문 설정은 null일 수 없습니다.")
    private Body body = new Body();

    /**
     * 로그 전송 방식 (kafka 또는 loki).
     * 기본값은 "kafka"입니다.
     */
    @NotNull(message = "로그 전송 방식은 null일 수 없습니다.")
    private String logDestination = "kafka"; // 기본값 설정 (예: kafka)

    /**
     * 설정된 값들을 검증하여 유효성을 확인합니다.
     * <p>
     * 본문 최대 크기가 0보다 작은 경우 예외를 발생시킵니다.
     * </p>
     *
     * @throws IllegalArgumentException 본문 최대 크기가 0 이하일 경우
     */
    public void validateProperties() {
        if (body.getMaxSize() <= 0) {
            throw new IllegalArgumentException("로그 본문의 최대 크기는 0보다 커야 합니다.");
        }
    }

    /**
     * 요청 본문에 대한 설정을 담고 있는 내부 클래스입니다.
     */
    @Getter
    @Setter
    public static class RequestBody {
        /**
         * 요청 본문을 잘라낼지 여부를 설정합니다.
         * 기본값은 false입니다.
         */
        private boolean truncate = false; // 기본값 설정
    }

    /**
     * 응답 본문에 대한 설정을 담고 있는 내부 클래스입니다.
     */
    @Getter
    @Setter
    public static class ResponseBody {
        /**
         * 응답 본문을 잘라낼지 여부를 설정합니다.
         * 기본값은 false입니다.
         */
        private boolean truncate = false; // 기본값 설정
    }

    /**
     * 로그 본문에 대한 설정을 담고 있는 내부 클래스입니다.
     */
    @Getter
    @Setter
    public static class Body {
        /**
         * 로그 본문의 최대 크기 (바이트 단위).
         * 기본값은 1024입니다.
         */
        @Min(value = 1, message = "로그 본문의 최대 크기는 0보다 커야 합니다.")
        private int maxSize = 1024; // 기본값 설정 (예: 1024 바이트)
    }
}
