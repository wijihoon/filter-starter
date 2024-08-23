package shinhancard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shinhancard.filter.CsrfFilter;
import shinhancard.properties.CsrfProperties;

/**
 * CSRF(크로스 사이트 요청 위조) 필터와 관련된 설정을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 Spring의 {@link Configuration} 어노테이션을 사용하여 CSRF 필터를 빈으로 등록합니다.
 * </p>
 */
@Configuration
public class CsrfConfig {

    /**
     * CSRF 필터를 빈으로 생성하여 Spring 컨텍스트에 등록합니다.
     *
     * @param csrfProperties CSRF 검사에 필요한 설정 정보를 담고 있는 {@link CsrfProperties} 객체
     * @return CSRF 공격을 탐지하고 방지하는 {@link CsrfFilter} 객체
     */
    @Bean
    public CsrfFilter csrfFilter(CsrfProperties csrfProperties) {
        return new CsrfFilter(csrfProperties); // CsrfProperties를 사용하여 CsrfFilter 객체를 생성합니다.
    }
}
