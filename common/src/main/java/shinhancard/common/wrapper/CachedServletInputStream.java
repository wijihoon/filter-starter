package shinhancard.common.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;

/**
 * 요청 본문을 캐싱하여 여러 번 읽을 수 있도록 하는 클래스입니다.
 */
public class CachedServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream inputStream;

    public CachedServletInputStream(byte[] data) {
        this.inputStream = new ByteArrayInputStream(data);
    }

    @Override
    public boolean isFinished() {
        return inputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        // 필요 시 구현 가능
    }

    @Override
    public int read() {
        return inputStream.read();
    }
}
