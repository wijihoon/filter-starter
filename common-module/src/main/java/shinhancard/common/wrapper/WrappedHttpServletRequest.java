package shinhancard.common.wrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * 요청 본문을 캐싱하여 여러 번 읽을 수 있도록 {@link HttpServletRequest}를 래핑하는 클래스입니다.
 * <p>
 * 이 클래스는 요청 본문을 바이트 배열로 저장하고, 이를 {@link CachedServletInputStream}을 통해 다시 읽을 수 있도록 합니다.
 * 또한, 요청 본문을 문자열로 변환하는 메서드를 제공합니다.
 * </p>
 */
public class WrappedHttpServletRequest extends HttpServletRequestWrapper {

	private final byte[] body; // 요청 본문을 저장하는 바이트 배열

	/**
	 * {@link WrappedHttpServletRequest}의 생성자입니다.
	 * <p>
	 * 주어진 {@link HttpServletRequest}로부터 요청 본문을 읽어 바이트 배열로 저장합니다.
	 * </p>
	 *
	 * @param request 원본 {@link HttpServletRequest}
	 * @throws IOException 요청 본문을 읽는 동안 I/O 오류가 발생한 경우
	 */
	public WrappedHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);
		this.body = request.getInputStream().readAllBytes();
	}

	/**
	 * 캐시된 요청 본문을 사용하여 {@link ServletInputStream}을 반환합니다.
	 *
	 * @return 요청 본문을 읽기 위한 {@link ServletInputStream}
	 */
	@Override
	public ServletInputStream getInputStream() {
		return new CachedServletInputStream(this.body);
	}

	/**
	 * 요청 본문을 문자열로 변환하여 반환합니다.
	 * <p>
	 * 요청 본문은 UTF-8 문자 집합을 사용하여 문자열로 변환됩니다.
	 * </p>
	 *
	 * @return 요청 본문을 문자열로 변환한 결과
	 */
	public String getBody() {
		return new String(this.body, StandardCharsets.UTF_8);
	}
}
