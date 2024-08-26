package shinhancard.common.io;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드를 정의하는 열거형입니다.
 * <p>
 * 각 에러 코드에는 고유 식별자, 설명 메시지, HTTP 상태 코드가 포함되어 있으며,
 * 애플리케이션에서 에러 처리를 통합적으로 관리할 수 있도록 합니다.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	/**
	 * CORS 출처 정책 위반 오류 코드.
	 * <p>
	 * 요청의 출처가 CORS 정책에서 허용되지 않는 경우 발생합니다.
	 * </p>
	 */
	CORS_ORIGIN_POLICY_VIOLATION("CORS_ORIGIN_POLICY_VIOLATION", "CORS 출처 정책 위반", HttpStatus.FORBIDDEN),

	/**
	 * CORS 메서드 정책 위반 오류 코드.
	 * <p>
	 * 요청에 사용된 HTTP 메서드가 CORS 정책에서 허용되지 않는 경우 발생합니다.
	 * </p>
	 */
	CORS_METHOD_POLICY_VIOLATION("CORS_METHOD_POLICY_VIOLATION", "CORS 메서드 정책 위반", HttpStatus.FORBIDDEN),

	/**
	 * CORS 헤더 정책 위반 오류 코드.
	 * <p>
	 * 요청에 포함된 헤더가 CORS 정책에서 허용되지 않는 경우 발생합니다.
	 * </p>
	 */
	CORS_HEADERS_POLICY_VIOLATION("CORS_HEADERS_POLICY_VIOLATION", "CORS 헤더 정책 위반", HttpStatus.FORBIDDEN),

	/**
	 * XSS 공격이 감지된 오류 코드.
	 * <p>
	 * 요청에서 XSS 공격이 감지된 경우 발생합니다.
	 * </p>
	 */
	XSS_DETECTED("XSS_DETECTED", "XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

	/**
	 * 요청 파라미터에서 XSS 공격이 감지된 오류 코드.
	 * <p>
	 * 요청 파라미터에서 XSS 공격이 감지된 경우 발생합니다.
	 * </p>
	 */
	XSS_IN_PARAMETER("XSS_IN_PARAMETER", "요청 파라미터에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

	/**
	 * 쿠키에서 XSS 공격이 감지된 오류 코드.
	 * <p>
	 * 쿠키에서 XSS 공격이 감지된 경우 발생합니다.
	 * </p>
	 */
	XSS_IN_COOKIE("XSS_IN_COOKIE", "쿠키에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

	/**
	 * 요청 본문에서 XSS 공격이 감지된 오류 코드.
	 * <p>
	 * 요청 본문에서 XSS 공격이 감지된 경우 발생합니다.
	 * </p>
	 */
	XSS_IN_BODY("XSS_IN_BODY", "요청 본문에서 XSS 공격이 감지되었습니다", HttpStatus.FORBIDDEN),

	/**
	 * 요청 파라미터에서 SQL 인젝션이 감지된 오류 코드.
	 * <p>
	 * 요청 파라미터에서 SQL 인젝션이 감지된 경우 발생합니다.
	 * </p>
	 */
	SQL_INJECTION_PARAMETER_DETECTED("SQL_INJECTION_PARAMETER_DETECTED", "파라미터에서 SQL 인젝션이 감지되었습니다",
		HttpStatus.BAD_REQUEST),

	/**
	 * 요청 본문에서 SQL 인젝션이 감지된 오류 코드.
	 * <p>
	 * 요청 본문에서 SQL 인젝션이 감지된 경우 발생합니다.
	 * </p>
	 */
	SQL_INJECTION_BODY_DETECTED("SQL_INJECTION_BODY_DETECTED", "요청 본문에서 SQL 인젝션이 감지되었습니다", HttpStatus.BAD_REQUEST),

	/**
	 * 쿠키에서 SQL 인젝션이 감지된 오류 코드.
	 * <p>
	 * 쿠키에서 SQL 인젝션이 감지된 경우 발생합니다.
	 * </p>
	 */
	SQL_INJECTION_COOKIE_DETECTED("SQL_INJECTION_COOKIE_DETECTED", "쿠키에서 SQL 인젝션이 감지되었습니다", HttpStatus.BAD_REQUEST);

	private final String code; // 에러 코드
	private final String message; // 에러 메시지
	private final HttpStatus httpStatus; // HTTP 상태 코드
}
