package shinhancard.common.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

/**
 * 응답 데이터를 표현하는 불변 객체입니다.
 *
 * @param <T> 응답 데이터의 타입
 */
public record ResponseVo<T>(
        boolean success, // 요청 성공 여부
        String message, // 응답 메시지
        T data, // 응답 데이터
        HttpStatus status // HTTP 상태 코드
) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 성공 응답을 생성합니다.
     * <p>
     * 이 메서드는 기본적인 성공 응답을 생성합니다. 응답 데이터가 포함되어 있으며,
     * 기본적으로 HTTP 상태 코드는 200 OK로 설정됩니다.
     * </p>
     *
     * @param data 응답 데이터. 성공적인 요청에 대한 데이터를 포함합니다.
     * @param <T>  응답 데이터의 타입
     * @return 성공 응답을 담은 ResponseVo 객체
     */
    public static <T> ResponseVo<T> success(T data) {
        return new ResponseVo<>(true, "Success", data, HttpStatus.OK);
    }

    /**
     * 실패 응답을 생성합니다.
     * <p>
     * 이 메서드는 실패 응답을 생성합니다. 실패 메시지와 HTTP 상태 코드가 포함됩니다.
     * </p>
     *
     * @param message 실패 메시지. 요청 처리 중 발생한 오류에 대한 설명입니다.
     * @param status  HTTP 상태 코드. 요청 처리 결과에 따른 상태 코드를 설정합니다.
     * @param <T>     응답 데이터의 타입. 이 메서드에서는 데이터가 null로 설정됩니다.
     * @return 실패 응답을 담은 ResponseVo 객체
     */
    public static <T> ResponseVo<T> error(String message, HttpStatus status) {
        return new ResponseVo<>(false, message, null, status);
    }

    /**
     * 헤더를 포함한 성공 응답을 생성합니다.
     * <p>
     * 이 메서드는 기본적인 성공 응답을 생성합니다. 응답 데이터가 포함되어 있으며,
     * 기본적으로 HTTP 상태 코드는 200 OK로 설정됩니다. 현재 메서드에서는 헤더 처리 로직이
     * 포함되어 있지 않으며, 동일한 응답을 반환합니다.
     * </p>
     *
     * @param data 응답 데이터. 성공적인 요청에 대한 데이터를 포함합니다.
     * @param <T>  응답 데이터의 타입
     * @return 성공 응답을 담은 ResponseVo 객체
     */
    public static <T> ResponseVo<T> successWithHeader(T data) {
        return new ResponseVo<>(true, "Success", data, HttpStatus.OK);
    }

    /**
     * JSON 형식의 문자열을 반환합니다.
     *
     * @return JSON 형식의 문자열
     */
    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert ResponseVo to JSON string", e);
        }
    }
}
