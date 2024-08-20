package com.shinhancard.toss.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * XSS (Cross-Site Scripting) 공격을 방지하기 위한 필터입니다.
 * <p>
 * 이 필터는 HTTP 요청의 파라미터와 헤더에서 XSS 공격을 방지하기 위해
 * 모든 입력 값을 HTML로 인코딩합니다.
 * </p>
 */
@Component
public class XssFilter extends org.springframework.web.filter.GenericFilterBean {

	/**
	 * HTTP 요청을 필터링하여 XSS 공격을 방지합니다.
	 * <p>
	 * {@link HttpServletRequest}를 래핑하여 파라미터와 헤더 값을 HTML로 인코딩합니다.
	 * 필터 체인을 계속 진행하기 전에 인코딩된 요청을 전달합니다.
	 * </p>
	 *
	 * @param request  HTTP 요청
	 * @param response HTTP 응답
	 * @param chain    필터 체인
	 * @throws IOException      입출력 예외
	 * @throws ServletException 서블릿 예외
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;

		// XSS를 필터링하기 위해 요청을 래핑합니다.
		XssRequestWrapper requestWrapper = new XssRequestWrapper(req);

		// 필터 체인을 계속 진행합니다.
		chain.doFilter(requestWrapper, res);
	}

	/**
	 * XSS 필터링이 적용된 요청을 래핑하는 클래스입니다.
	 * <p>
	 * HTTP 요청의 파라미터와 헤더 값을 HTML로 인코딩하여 XSS 공격을 방지합니다.
	 * 요청 본문도 처리하여 XSS 공격을 방지할 수 있습니다.
	 * </p>
	 */
	static class XssRequestWrapper extends HttpServletRequestWrapper {

		/**
		 * Constructs a request object wrapping the given request.
		 *
		 * @param request The request to wrap
		 * @throws IllegalArgumentException if the request is null
		 */
		public XssRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		/**
		 * 요청 파라미터 값을 HTML로 인코딩하여 반환합니다.
		 * <p>
		 * HTML 인코딩 외에도 자바스크립트, URL, CSS 삽입을 방지하기 위한
		 * 추가적인 처리를 고려합니다.
		 * </p>
		 *
		 * @param name 파라미터 이름
		 * @return HTML로 인코딩된 파라미터 값
		 */
		@Override
		public String getParameter(String name) {
			String value = super.getParameter(name);
			return HtmlUtils.htmlEscape(value); // XSS 공격 방지
		}

		/**
		 * 요청 파라미터 배열을 HTML로 인코딩하여 반환합니다.
		 * <p>
		 * HTML 인코딩 외에도 자바스크립트, URL, CSS 삽입을 방지하기 위한
		 * 추가적인 처리를 고려합니다.
		 * </p>
		 *
		 * @param name 파라미터 이름
		 * @return HTML로 인코딩된 파라미터 값 배열
		 */
		@Override
		public String[] getParameterValues(String name) {
			String[] values = super.getParameterValues(name);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					values[i] = HtmlUtils.htmlEscape(values[i]); // XSS 공격 방지
				}
			}
			return values;
		}

		/**
		 * 요청 헤더 값을 HTML로 인코딩하여 반환합니다.
		 * <p>
		 * 헤더 인젝션 공격을 방지하기 위해 헤더 값을 HTML로 인코딩합니다.
		 * </p>
		 *
		 * @param name 헤더 이름
		 * @return HTML로 인코딩된 헤더 값
		 */
		@Override
		public String getHeader(String name) {
			String value = super.getHeader(name);
			return HtmlUtils.htmlEscape(value); // XSS 공격 방지
		}

		/**
		 * 요청 헤더 이름을 반환합니다.
		 * <p>
		 * 헤더 이름 목록을 반환하며, HTML로 인코딩할 필요는 없습니다.
		 * </p>
		 *
		 * @return 헤더 이름 목록
		 */
		@Override
		public Enumeration<String> getHeaderNames() {
			return super.getHeaderNames();
		}

		/**
		 * 요청 헤더 값을 배열로 반환합니다.
		 * <p>
		 * 각 헤더의 값은 HTML로 인코딩되어 반환됩니다.
		 * </p>
		 *
		 * @param name 헤더 이름
		 * @return HTML로 인코딩된 헤더 값 배열
		 */
		@Override
		public Enumeration<String> getHeaders(String name) {
			Enumeration<String> headerValues = super.getHeaders(name);
			Map<String, String> encodedHeaders = new HashMap<>();
			while (headerValues.hasMoreElements()) {
				String headerValue = headerValues.nextElement();
				encodedHeaders.put(name, HtmlUtils.htmlEscape(headerValue)); // XSS 공격 방지
			}
			return Collections.enumeration(encodedHeaders.values());
		}

		/**
		 * 요청 본문을 HTML로 인코딩하여 반환합니다.
		 * <p>
		 * 요청 본문을 HTML로 인코딩하여 XSS 공격을 방지합니다.
		 * </p>
		 *
		 * @return HTML로 인코딩된 본문
		 * @throws IOException 입출력 예외
		 */
		@Override
		public BufferedReader getReader() throws IOException {
			BufferedReader reader = super.getReader();
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(HtmlUtils.htmlEscape(line)).append(System.lineSeparator());
			}
			return new BufferedReader(new StringReader(sb.toString()));
		}
	}
}
