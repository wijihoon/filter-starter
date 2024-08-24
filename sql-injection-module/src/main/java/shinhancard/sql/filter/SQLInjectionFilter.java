package shinhancard.sql.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import shinhancard.common.wrapper.WrappedHttpServletRequest;
import shinhancard.sql.properties.SQLInjectionProperties;

import java.io.IOException;
import java.util.Arrays;

/**
 * SQL 인젝션을 방지하는 필터 클래스입니다.
 * <p>
 * 요청 본문, 파라미터 및 쿠키에서 SQL 인젝션 패턴을 검사합니다.
 * </p>
 */
@RequiredArgsConstructor
public class SQLInjectionFilter extends OncePerRequestFilter {

    private final SQLInjectionProperties sqlInjectionProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청을 래핑하여 본문을 캐싱합니다.
        WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);

        // SQL 인젝션 검사
        validateRequestParameters(wrappedRequest);
        validateRequestBody(wrappedRequest);
        validateCookies(wrappedRequest);

        // 필터 체인을 계속 진행합니다.
        filterChain.doFilter(wrappedRequest, response);
    }

    private void validateRequestParameters(WrappedHttpServletRequest request) {
        request.getParameterMap().forEach((name, values) -> {
            Arrays.stream(values).forEach(value -> {
                if (value != null && sqlInjectionProperties.getCompiledPattern().matcher(value).find()) {
                    try {
                        throw new ServletException("SQL Injection detected in parameter: " + name);
                    } catch (ServletException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
    }

    private void validateRequestBody(WrappedHttpServletRequest request) throws ServletException {
        String body = request.getBody();
        if (sqlInjectionProperties.getCompiledPattern().matcher(body).find()) {
            throw new ServletException("SQL Injection detected in request body.");
        }
    }

    private void validateCookies(WrappedHttpServletRequest request) {
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies()).forEach(cookie -> {
                if (sqlInjectionProperties.getCompiledPattern().matcher(cookie.getValue()).find()) {
                    try {
                        throw new ServletException("SQL Injection detected in cookie: " + cookie.getName());
                    } catch (ServletException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
