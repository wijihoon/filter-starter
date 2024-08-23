package shinhancard.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import shinhancard.properties.CsrfProperties;

/**
 * CSRF 보호를 위한 필터입니다.
 * 이 필터는 보안 관련 헤더를 응답에 추가합니다.
 */
@Slf4j
public class CsrfFilter extends OncePerRequestFilter {

	private final CsrfProperties csrfProperties;

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

		// 보안 관련 헤더 추가 (값이 설정되어 있는 경우에만)
		if (csrfProperties.getXFrameOptions() != null && !csrfProperties.getXFrameOptions().isEmpty()) {
			response.setHeader("X-Frame-Options", csrfProperties.getXFrameOptions());
			log.debug("X-Frame-Options 헤더가 설정되었습니다: {}", csrfProperties.getXFrameOptions());
		}
		if (csrfProperties.getXXssProtection() != null && !csrfProperties.getXXssProtection().isEmpty()) {
			response.setHeader("X-XSS-Protection", csrfProperties.getXXssProtection());
			log.debug("X-XSS-Protection 헤더가 설정되었습니다: {}", csrfProperties.getXXssProtection());
		}
		if (csrfProperties.getXContentTypeOptions() != null && !csrfProperties.getXContentTypeOptions().isEmpty()) {
			response.setHeader("X-Content-Type-Options", csrfProperties.getXContentTypeOptions());
			log.debug("X-Content-Type-Options 헤더가 설정되었습니다: {}", csrfProperties.getXContentTypeOptions());
		}

		// 다음 필터로 이동
		filterChain.doFilter(request, response);
	}
}