package shinhancard.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import shinhancard.properties.LokiProperties;
import shinhancard.service.LogService;

import java.io.IOException;

/**
 * Loki 서버에 로그 메시지를 전송하는 서비스입니다.
 * <p>
 * 이 서비스는 {@link WebClient}를 사용하여 Loki 서버에 HTTP POST 요청을 보내 로그를 전송합니다.
 * </p>
 */
@Service
public class LokiService implements LogService {

    private final WebClient webClient;
    private final LokiProperties lokiProperties;

    /**
     * LokiService의 생성자입니다.
     * <p>
     * LokiProperties를 주입받아 Loki 서버의 URL을 설정하고, WebClient를 초기화합니다.
     * </p>
     *
     * @param lokiProperties Loki 서버의 설정 속성
     */
    public LokiService(LokiProperties lokiProperties) {
        this.lokiProperties = lokiProperties;
        this.webClient = WebClient.builder().build();
    }

    /**
     * 로그 메시지를 Loki 서버에 전송합니다.
     * <p>
     * 이 메서드는 Loki 서버에 HTTP POST 요청을 보내 로그 메시지를 전송합니다.
     * 요청의 본문으로 로그 메시지를 전송하며, 응답을 블록킹 호출로 기다립니다.
     * </p>
     *
     * @param logJson 전송할 로그 메시지 (JSON 형식)
     * @throws IOException 전송 중 I/O 오류가 발생한 경우
     */
    @Override
    public void sendLog(String logJson) throws IOException {
        try {
            webClient.post()
                    .uri(lokiProperties.getUrl())
                    .bodyValue(logJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 블록킹 호출로 응답을 기다림
        } catch (Exception e) {
            throw new IOException("로그 전송 중 오류 발생", e);
        }
    }
}
