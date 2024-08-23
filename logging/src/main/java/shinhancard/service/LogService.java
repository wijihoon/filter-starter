package shinhancard.service;

import java.io.IOException;

/**
 * 로그 메시지를 전송하는 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 로그 메시지를 특정 로그 전송 시스템으로 전송하는 메서드를 정의합니다.
 * 구현 클래스는 이 메서드를 통해 로그 메시지를 전송하는 구체적인 방식을 정의해야 합니다.
 * </p>
 */
public interface LogService {

    /**
     * 로그 메시지를 전송합니다.
     * <p>
     * 이 메서드는 로그 메시지를 지정된 로그 전송 시스템으로 전송합니다.
     * 로그 메시지는 JSON 형식으로 제공되며, 메서드는 전송 중 발생할 수 있는 I/O 오류를 처리합니다.
     * </p>
     *
     * @param logJson 전송할 로그 메시지 (JSON 형식)
     * @throws IOException 전송 중 I/O 오류가 발생한 경우
     */
    void sendLog(String logJson) throws IOException;
}
