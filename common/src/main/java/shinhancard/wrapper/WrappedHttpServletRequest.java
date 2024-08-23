package shinhancard.wrapper;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 요청 본문을 캐싱하여 여러 번 읽을 수 있도록 래핑하는 클래스입니다.
 */
public class WrappedHttpServletRequest extends HttpServletRequestWrapper {
    private final byte[] body;

    public WrappedHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.body = request.getInputStream().readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(this.body);
    }

    public String getBody() {
        return new String(this.body, StandardCharsets.UTF_8);
    }
}