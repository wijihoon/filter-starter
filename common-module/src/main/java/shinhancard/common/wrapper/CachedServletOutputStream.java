package shinhancard.common.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

/**
 * 응답 본문을 캐싱하여 여러 번 읽을 수 있도록 하는 클래스입니다.
 * <p>
 * 이 클래스는 {@link ServletOutputStream}을 확장하여 응답 본문을 내부 버퍼에 저장합니다.
 * </p>
 */
public class CachedServletOutputStream extends ServletOutputStream {

	private final ByteArrayOutputStream buffer; // 응답 본문을 저장할 버퍼

	/**
	 * CachedServletOutputStream 생성자
	 *
	 * @param buffer 응답 본문을 저장할 버퍼
	 */
	public CachedServletOutputStream(ByteArrayOutputStream buffer) {
		this.buffer = buffer; // 버퍼를 이용해 CachedServletOutputStream 생성
	}

	@Override
	public boolean isReady() {
		return true; // 스트림이 준비되었는지 확인
	}

	@Override
	public void setWriteListener(WriteListener listener) {
		// 비동기 처리가 필요 없는 경우 빈 구현
	}

	@Override
	public void write(int b) throws IOException {
		buffer.write(b); // 버퍼에 바이트를 기록
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		buffer.write(b, off, len); // 바이트 배열의 일부를 버퍼에 기록
	}
}
