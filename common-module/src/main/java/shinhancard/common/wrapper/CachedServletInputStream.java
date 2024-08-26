package shinhancard.common.wrapper;

import java.io.ByteArrayInputStream;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

/**
 * 요청 본문을 캐싱하여 여러 번 읽을 수 있도록 하는 {@link ServletInputStream} 구현체입니다.
 * <p>
 * 이 클래스는 요청 본문을 바이트 배열로 저장하고, 이를 {@link ByteArrayInputStream}을 통해 읽을 수 있도록 합니다.
 * </p>
 */
public class CachedServletInputStream extends ServletInputStream {
	private final ByteArrayInputStream inputStream;

	/**
	 * {@link CachedServletInputStream}의 생성자입니다.
	 * <p>
	 * 주어진 바이트 배열을 사용하여 {@link ByteArrayInputStream}을 초기화합니다.
	 * </p>
	 *
	 * @param data 요청 본문을 담고 있는 바이트 배열
	 */
	public CachedServletInputStream(byte[] data) {
		this.inputStream = new ByteArrayInputStream(data);
	}

	/**
	 * 스트림이 끝까지 읽었는지를 확인합니다.
	 *
	 * @return 스트림의 끝에 도달했으면 {@code true}, 그렇지 않으면 {@code false}
	 */
	@Override
	public boolean isFinished() {
		return inputStream.available() == 0;
	}

	/**
	 * 스트림이 읽기 가능한지 확인합니다.
	 *
	 * @return 항상 {@code true}를 반환합니다.
	 */
	@Override
	public boolean isReady() {
		return true;
	}

	/**
	 * 읽기 리스너를 설정합니다.
	 * <p>
	 * 이 메서드는 현재 구현되지 않았습니다. 필요에 따라 구현할 수 있습니다.
	 * </p>
	 *
	 * @param readListener 읽기 리스너
	 */
	@Override
	public void setReadListener(ReadListener readListener) {
		// 필요 시 구현 가능
	}

	/**
	 * 스트림에서 한 바이트를 읽습니다.
	 *
	 * @return 읽은 바이트, 더 이상 읽을 바이트가 없으면 {@code -1}
	 */
	@Override
	public int read() {
		return inputStream.read();
	}
}
