package shinhancard.io;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드를 정의하는 열거형입니다.
 * <p>
 * 각 에러 코드에 대해 HTTP 상태 코드와 메시지를 포함하며, 에러 코드와 관련된 상세 정보를 제공하여 에러 처리를 통합적으로 관리할 수 있도록 합니다.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /**
     * CORS 출처 정책 위반.
     */
    CORS_ORIGIN_POLICY_VIOLATION("CORS_ORIGIN_POLICY_VIOLATION", "CORS 출처 정책 위반", HttpStatus.FORBIDDEN),

    /**
     * CORS 메서드 정책 위반.
     */
    CORS_METHOD_POLICY_VIOLATION("CORS_METHOD_POLICY_VIOLATION", "CORS 메서드 정책 위반", HttpStatus.FORBIDDEN),

    /**
     * CORS 헤더 정책 위반.
     */
    CORS_HEADERS_POLICY_VIOLATION("CORS_HEADERS_POLICY_VIOLATION", "CORS 헤더 정책 위반", HttpStatus.FORBIDDEN),

    /**
     * XSS 공격이 감지되었습니다.
     */
    XSS_DETECTED("XSS_DETECTED", "XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

    /**
     * 요청 파라미터에서 XSS 공격이 감지되었습니다.
     */
    XSS_IN_PARAMETER("XSS_IN_PARAMETER", "요청 파라미터에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

    /**
     * 쿠키에서 XSS 공격이 감지되었습니다.
     */
    XSS_IN_COOKIE("XSS_IN_COOKIE", "쿠키에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

    /**
     * 요청 본문에서 XSS 공격이 감지되었습니다.
     */
    XSS_IN_BODY("XSS_IN_BODY", "요청 본문에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN);

    private final String code; // 에러 코드
    private final String message; // 에러 메시지
    private final HttpStatus httpStatus; // HTTP 상태 코드
}
