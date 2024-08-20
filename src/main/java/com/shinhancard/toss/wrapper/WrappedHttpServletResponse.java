package com.shinhancard.toss.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * 응답 본문을 캐싱하여 여러 번 읽을 수 있도록 래핑하는 클래스입니다.
 */
public class WrappedHttpServletResponse extends HttpServletResponseWrapper {
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private final CachedServletOutputStream outputStream = new CachedServletOutputStream(buffer);
	private final PrintWriter writer = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);

	public WrappedHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public ServletOutputStream getOutputStream() {
		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() {
		return this.writer;
	}

	public String getBody() {
		return buffer.toString(StandardCharsets.UTF_8);
	}
}
