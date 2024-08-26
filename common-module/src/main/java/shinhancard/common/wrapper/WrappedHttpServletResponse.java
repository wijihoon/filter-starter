package shinhancard.common.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * 응답 본문을 캐싱하여 여러 번 읽을 수 있도록 {@link HttpServletResponse}를 래핑하는 클래스입니다.
 * <p>
 * 이 클래스는 응답 본문을 {@link ByteArrayOutputStream}에 저장하고, 이를 {@link CachedServletOutputStream}을 통해 처리합니다.
 * 또한, 응답 본문을 문자열로 변환하거나 버퍼를 리셋하는 기능을 제공합니다.
 * </p>
 */
public class WrappedHttpServletResponse extends HttpServletResponseWrapper {

	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(); // 응답 본문을 저장할 버퍼
	private final CachedServletOutputStream outputStream = new CachedServletOutputStream(buffer); // 캐시된 응답 출력 스트림

	/**
	 * {@link WrappedHttpServletResponse}의 생성자입니다.
	 * <p>
	 * 주어진 {@link HttpServletResponse}를 래핑하여 응답 본문을 캐싱할 수 있도록 초기화합니다.
	 * </p>
	 *
	 * @param response 원본 {@link HttpServletResponse}
	 */
	public WrappedHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	/**
	 * 캐시된 응답 본문을 사용하여 {@link ServletOutputStream}을 반환합니다.
	 *
	 * @return 응답 본문을 기록하기 위한 {@link ServletOutputStream}
	 */
	@Override
	public ServletOutputStream getOutputStream() {
		return this.outputStream;
	}

	/**
	 * 버퍼의 내용을 클라이언트로 전송합니다.
	 * <p>
	 * 버퍼에 저장된 모든 데이터를 원본 응답의 출력 스트림을 통해 클라이언트로 전송하고,
	 * 출력 스트림을 플러시하여 전송을 완료합니다.
	 * </p>
	 *
	 * @throws IOException 버퍼의 내용을 전송하는 동안 I/O 오류가 발생한 경우
	 */
	@Override
	public void flushBuffer() throws IOException {
		// 버퍼의 내용을 클라이언트로 전송
		byte[] content = buffer.toByteArray();
		HttpServletResponse originalResponse = (HttpServletResponse)getResponse();

		originalResponse.getOutputStream().write(content);
		originalResponse.getOutputStream().flush();
	}

	/**
	 * 응답의 상태를 리셋하고, 버퍼를 초기화합니다.
	 * <p>
	 * 원본 응답의 상태를 리셋하고, 버퍼를 비웁니다.
	 * </p>
	 */
	@Override
	public void reset() {
		super.reset();
		buffer.reset();
	}

	/**
	 * 응답의 버퍼를 리셋합니다.
	 * <p>
	 * 원본 응답의 버퍼를 리셋하고, 캐시된 버퍼를 비웁니다.
	 * </p>
	 */
	@Override
	public void resetBuffer() {
		super.resetBuffer();
		buffer.reset();
	}

	/**
	 * 캐시된 응답 본문을 문자열로 변환하여 반환합니다.
	 * <p>
	 * 응답 본문은 UTF-8 문자 집합을 사용하여 문자열로 변환됩니다.
	 * </p>
	 *
	 * @return 응답 본문을 문자열로 변환한 결과
	 */
	public String getBody() {
		return buffer.toString(StandardCharsets.UTF_8);
	}
}
