package shinhancard.csrf.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import shinhancard.csrf.properties.CsrfProperties;

import java.io.IOException;

/**
 * CSRF 보호를 위한 필터입니다.
 * <p>
 * 이 필터는 보안 관련 헤더를 HTTP 응답에 추가합니다. 헤더의 값은 {@link CsrfProperties}에 설정된 값을 기반으로 합니다.
 * </p>
 */
@Slf4j
public class CsrfFilter extends OncePerRequestFilter {

    private final CsrfProperties csrfProperties;

    /**
     * 생성자 주입을 통해 {@link CsrfProperties}를 설정합니다.
     *
     * @param csrfProperties CSRF 보안 헤더 설정을 위한 프로퍼티 객체
     */
    @Autowired
    public CsrfFilter(CsrfProperties csrfProperties) {
        this.csrfProperties = csrfProperties;
    }

    /**
     * 보안 관련 헤더를 응답에 추가합니다.
     *
     * @param request     필터 체인에 전달된 {@link HttpServletRequest}
     * @param response    필터 체인에 전달된 {@link HttpServletResponse}
     * @param filterChain 필터 체인
     * @throws ServletException 필터 처리 중 발생할 수 있는 예외
     * @throws IOException      입출력 처리 중 발생할 수 있는 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 보안 관련 헤더 추가 (값이 설정된 경우에만)
        addHeaderIfNotEmpty(response, "X-Frame-Options", csrfProperties.getXFrameOptions());
        addHeaderIfNotEmpty(response, "X-XSS-Protection", csrfProperties.getXXssProtection());
        addHeaderIfNotEmpty(response, "X-Content-Type-Options", csrfProperties.getXContentTypeOptions());

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    /**
     * 헤더를 응답에 추가합니다. 헤더 값이 null이거나 비어있지 않은 경우에만 추가합니다.
     *
     * @param response    HTTP 응답 객체
     * @param header      추가할 헤더의 이름
     * @param headerValue 헤더의 값
     */
    private void addHeaderIfNotEmpty(HttpServletResponse response, String header, String headerValue) {
        if (headerValue != null && !headerValue.isEmpty()) {
            response.setHeader(header, headerValue);
            log.debug("{} 헤더가 설정되었습니다: {}", header, headerValue);
        }
    }
}
