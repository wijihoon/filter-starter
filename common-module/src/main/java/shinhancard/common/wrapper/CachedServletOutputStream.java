package shinhancard.common.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

/**
 * 응답 본문을 캐싱하여 여러 번 읽을 수 있도록 하는 {@link ServletOutputStream} 구현체입니다.
 * <p>
 * 이 클래스는 {@link ByteArrayOutputStream}을 사용하여 응답 본문을 내부 버퍼에 저장합니다.
 * </p>
 */
public class CachedServletOutputStream extends ServletOutputStream {

	private final ByteArrayOutputStream buffer; // 응답 본문을 저장할 버퍼

	/**
	 * {@link CachedServletOutputStream}의 생성자입니다.
	 * <p>
	 * 주어진 {@link ByteArrayOutputStream} 버퍼를 사용하여 {@code CachedServletOutputStream}을 초기화합니다.
	 * </p>
	 *
	 * @param buffer 응답 본문을 저장할 {@link ByteArrayOutputStream} 버퍼
	 */
	public CachedServletOutputStream(ByteArrayOutputStream buffer) {
		this.buffer = buffer; // 버퍼를 이용해 CachedServletOutputStream 생성
	}

	/**
	 * 스트림이 준비되었는지를 확인합니다.
	 *
	 * @return 항상 {@code true}를 반환합니다.
	 */
	@Override
	public boolean isReady() {
		return true; // 스트림이 준비되었는지 확인
	}

	/**
	 * 쓰기 리스너를 설정합니다.
	 * <p>
	 * 비동기 처리가 필요하지 않으므로 빈 구현을 제공합니다.
	 * </p>
	 *
	 * @param listener 쓰기 리스너
	 */
	@Override
	public void setWriteListener(WriteListener listener) {
		// 비동기 처리가 필요 없는 경우 빈 구현
	}

	/**
	 * 버퍼에 한 바이트를 기록합니다.
	 *
	 * @param b 기록할 바이트
	 * @throws IOException I/O 오류가 발생한 경우
	 */
	@Override
	public void write(int b) throws IOException {
		buffer.write(b); // 버퍼에 바이트를 기록
	}

	/**
	 * 버퍼에 바이트 배열의 일부를 기록합니다.
	 *
	 * @param b 기록할 바이트 배열
	 * @param off 바이트 배열에서 기록을 시작할 오프셋
	 * @param len 기록할 바이트 수
	 * @throws IOException I/O 오류가 발생한 경우
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		buffer.write(b, off, len); // 바이트 배열의 일부를 버퍼에 기록
	}
}
