package shinhancard.common.io;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 응답 코드를 정의하는 열거형입니다.
 * <p>
 * 각 응답 코드에는 4자리 숫자의 코드, 설명 메시지, HTTP 상태 코드가 포함되어 있으며,
 * 애플리케이션에서 응답 처리를 통합적으로 관리할 수 있도록 합니다.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum ResponseCode {

	/**
	 * 성공 응답 코드.
	 * <p>
	 * 요청이 성공적으로 처리된 경우 발생합니다.
	 * </p>
	 */
	SUCCESS("0000", "요청이 성공적으로 처리되었습니다.", HttpStatus.OK),

	/**
	 * CORS 출처 정책 위반 오류 코드.
	 * <p>
	 * 요청의 출처가 CORS 정책에서 허용되지 않는 경우 발생합니다.
	 * </p>
	 */
	CORS_ORIGIN_POLICY_VIOLATION("1001", "CORS 출처 정책 위반", HttpStatus.FORBIDDEN),

	/**
	 * CORS 메서드 정책 위반 오류 코드.
	 * <p>
	 * 요청에 사용된 HTTP 메서드가 CORS 정책에서 허용되지 않는 경우 발생합니다.
	 * </p>
	 */
	CORS_METHOD_POLICY_VIOLATION("1002", "CORS 메서드 정책 위반", HttpStatus.FORBIDDEN),

	/**
	 * CORS 헤더 정책 위반 오류 코드.
	 * <p>
	 * 요청에 포함된 헤더가 CORS 정책에서 허용되지 않는 경우 발생합니다.
	 * </p>
	 */
	CORS_HEADERS_POLICY_VIOLATION("1003", "CORS 헤더 정책 위반", HttpStatus.FORBIDDEN),

	/**
	 * XSS 공격이 감지된 오류 코드.
	 * <p>
	 * 요청에서 XSS 공격이 감지된 경우 발생합니다.
	 * </p>
	 */
	XSS_DETECTED("2001", "XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

	/**
	 * 요청 파라미터에서 XSS 공격이 감지된 오류 코드.
	 * <p>
	 * 요청 파라미터에서 XSS 공격이 감지된 경우 발생합니다.
	 * </p>
	 */
	XSS_IN_PARAMETER("2002", "요청 파라미터에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

	/**
	 * 쿠키에서 XSS 공격이 감지된 오류 코드.
	 * <p>
	 * 쿠키에서 XSS 공격이 감지된 경우 발생합니다.
	 * </p>
	 */
	XSS_IN_COOKIE("2003", "쿠키에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

	/**
	 * 요청 본문에서 XSS 공격이 감지된 오류 코드.
	 * <p>
	 * 요청 본문에서 XSS 공격이 감지된 경우 발생합니다.
	 * </p>
	 */
	XSS_IN_BODY("2004", "요청 본문에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

	/**
	 * 요청 파라미터에서 SQL 인젝션이 감지된 오류 코드.
	 * <p>
	 * 요청 파라미터에서 SQL 인젝션이 감지된 경우 발생합니다.
	 * </p>
	 */
	SQL_INJECTION_PARAMETER_DETECTED("3001", "파라미터에서 SQL 인젝션이 감지되었습니다", HttpStatus.BAD_REQUEST),

	/**
	 * 요청 본문에서 SQL 인젝션이 감지된 오류 코드.
	 * <p>
	 * 요청 본문에서 SQL 인젝션이 감지된 경우 발생합니다.
	 * </p>
	 */
	SQL_INJECTION_BODY_DETECTED("3002", "요청 본문에서 SQL 인젝션이 감지되었습니다", HttpStatus.BAD_REQUEST),

	/**
	 * 쿠키에서 SQL 인젝션이 감지된 오류 코드.
	 * <p>
	 * 쿠키에서 SQL 인젝션이 감지된 경우 발생합니다.
	 * </p>
	 */
	SQL_INJECTION_COOKIE_DETECTED("3003", "쿠키에서 SQL 인젝션이 감지되었습니다", HttpStatus.BAD_REQUEST),

	/**
	 * JSON 처리 오류 코드.
	 * <p>
	 * JSON을 처리하는 동안 오류가 발생한 경우 발생합니다.
	 * </p>
	 */
	JSON_PROCESSING_ERROR("4001", "JSON 처리 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

	/**
	 * 응답 코드.
	 * <p>
	 * 각 응답 코드의 4자리 숫자 코드입니다.
	 * </p>
	 */
	private final String code;

	/**
	 * 응답 메시지.
	 * <p>
	 * 응답에 대한 설명 메시지입니다.
	 * </p>
	 */
	private final String message;

	/**
	 * HTTP 상태 코드.
	 * <p>
	 * 응답 시 클라이언트에 반환될 HTTP 상태 코드입니다.
	 * </p>
	 */
	private final HttpStatus httpStatus;
}
