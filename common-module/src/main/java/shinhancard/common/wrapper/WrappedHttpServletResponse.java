package shinhancard.common.wrapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 응답 본문을 캐싱하여 여러 번 읽을 수 있도록 래핑하는 클래스입니다.
 */
public class WrappedHttpServletResponse extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final CachedServletOutputStream outputStream = new CachedServletOutputStream(buffer);

    public WrappedHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return this.outputStream;
    }

    @Override
    public void flushBuffer() throws IOException {
        // 버퍼의 내용을 클라이언트로 전송
        byte[] content = buffer.toByteArray();
        HttpServletResponse originalResponse = (HttpServletResponse) getResponse();

        originalResponse.getOutputStream().write(content);
        originalResponse.getOutputStream().flush();
    }

    @Override
    public void reset() {
        super.reset();
        buffer.reset();
    }

    @Override
    public void resetBuffer() {
        super.resetBuffer();
        buffer.reset();
    }

    public String getBody() {
        return buffer.toString(StandardCharsets.UTF_8);
    }
}