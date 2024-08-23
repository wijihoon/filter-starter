package shinhancard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shinhancard.filter.XssFilter;
import shinhancard.properties.XssProperties;

/**
 * XSS 필터와 관련된 설정을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 Spring의 {@link Configuration} 어노테이션을 사용하여 XSS 필터를 빈으로 등록합니다.
 * </p>
 */
@Configuration
public class XssConfig {

    /**
     * XssFilter 빈을 생성하여 Spring 컨텍스트에 등록합니다.
     *
     * @param xssProperties XSS 검사에 필요한 설정 정보를 담고 있는 {@link XssProperties} 객체
     * @return XSS 공격을 탐지하고 방지하는 {@link XssFilter} 객체
     */
    @Bean
    public XssFilter xssFilter(XssProperties xssProperties) {
        return new XssFilter(xssProperties); // XssProperties를 사용하여 XssFilter 객체를 생성합니다.
    }
}
