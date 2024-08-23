package shinhancard.io;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드를 정의하는 열거형입니다.
 * <p>
 * 각 에러 코드에 대해 HTTP 상태 코드와 메시지를 포함합니다. 이 열거형은 에러 코드와 관련된
 * 상세 정보를 제공하여 에러 처리를 통합적으로 관리할 수 있도록 합니다.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	/**
	 * 잘못된 자격 증명입니다.
	 */
	INVALID_CREDENTIALS("INVALID_CREDENTIALS", "잘못된 자격 증명입니다", HttpStatus.UNAUTHORIZED),

	/**
	 * 토큰이 유효하지 않습니다.
	 */
	TOKEN_INVALID("TOKEN_INVALID", "토큰이 유효하지 않습니다", HttpStatus.FORBIDDEN),

	/**
	 * Redis와의 상호작용 중 오류가 발생했습니다.
	 */
	REDIS_ERROR("REDIS_ERROR", "Redis와의 상호작용 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

	/**
	 * 잘못된 요청입니다.
	 */
	BAD_REQUEST("BAD_REQUEST", "잘못된 요청입니다", HttpStatus.BAD_REQUEST),

	/**
	 * 잘못된 요청입니다.
	 */
	INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),

	/**
	 * 리소스를 찾을 수 없습니다.
	 */
	NOT_FOUND("NOT_FOUND", "리소스를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

	/**
	 * 서버 내부 오류가 발생했습니다.
	 */
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

	/**
	 * 계속 진행하십시오.
	 */
	CONTINUE("CONTINUE", "계속 진행하십시오", HttpStatus.CONTINUE),

	/**
	 * 프로토콜을 전환하고 있습니다.
	 */
	SWITCHING_PROTOCOLS("SWITCHING_PROTOCOLS", "프로토콜을 전환하고 있습니다", HttpStatus.SWITCHING_PROTOCOLS),

	/**
	 * 처리 중입니다.
	 */
	PROCESSING("PROCESSING", "처리 중입니다", HttpStatus.PROCESSING),

	/**
	 * 조기 힌트.
	 */
	EARLY_HINTS("EARLY_HINTS", "조기 힌트", HttpStatus.EARLY_HINTS),

	/**
	 * 요청이 성공적으로 처리되었습니다.
	 */
	OK("OK", "요청이 성공적으로 처리되었습니다", HttpStatus.OK),

	/**
	 * 자원이 성공적으로 생성되었습니다.
	 */
	CREATED("CREATED", "자원이 성공적으로 생성되었습니다", HttpStatus.CREATED),

	/**
	 * 요청이 수락되었습니다.
	 */
	ACCEPTED("ACCEPTED", "요청이 수락되었습니다", HttpStatus.ACCEPTED),

	/**
	 * 비권한 정보.
	 */
	NON_AUTHORITATIVE_INFORMATION("NON_AUTHORITATIVE_INFORMATION", "비권한 정보", HttpStatus.NON_AUTHORITATIVE_INFORMATION),

	/**
	 * 내용 없음.
	 */
	NO_CONTENT("NO_CONTENT", "내용 없음", HttpStatus.NO_CONTENT),

	/**
	 * 내용을 재설정하십시오.
	 */
	RESET_CONTENT("RESET_CONTENT", "내용을 재설정하십시오", HttpStatus.RESET_CONTENT),

	/**
	 * 부분 내용.
	 */
	PARTIAL_CONTENT("PARTIAL_CONTENT", "부분 내용", HttpStatus.PARTIAL_CONTENT),

	/**
	 * 다중 상태.
	 */
	MULTI_STATUS("MULTI_STATUS", "다중 상태", HttpStatus.MULTI_STATUS),

	/**
	 * 이미 보고되었습니다.
	 */
	ALREADY_REPORTED("ALREADY_REPORTED", "이미 보고되었습니다", HttpStatus.ALREADY_REPORTED),

	/**
	 * 사용됨.
	 */
	IM_USED("IM_USED", "사용됨", HttpStatus.IM_USED),

	/**
	 * 다수의 선택.
	 */
	MULTIPLE_CHOICES("MULTIPLE_CHOICES", "다수의 선택", HttpStatus.MULTIPLE_CHOICES),

	/**
	 * 영구적으로 이동되었습니다.
	 */
	MOVED_PERMANENTLY("MOVED_PERMANENTLY", "영구적으로 이동되었습니다", HttpStatus.MOVED_PERMANENTLY),

	/**
	 * 찾았습니다.
	 */
	FOUND("FOUND", "찾았습니다", HttpStatus.FOUND),

	/**
	 * 다른 곳을 확인하십시오.
	 */
	SEE_OTHER("SEE_OTHER", "다른 곳을 확인하십시오", HttpStatus.SEE_OTHER),

	/**
	 * 수정되지 않았습니다.
	 */
	NOT_MODIFIED("NOT_MODIFIED", "수정되지 않았습니다", HttpStatus.NOT_MODIFIED),

	/**
	 * 임시 리디렉션.
	 */
	TEMPORARY_REDIRECT("TEMPORARY_REDIRECT", "임시 리디렉션", HttpStatus.TEMPORARY_REDIRECT),

	/**
	 * 영구 리디렉션.
	 */
	PERMANENT_REDIRECT("PERMANENT_REDIRECT", "영구 리디렉션", HttpStatus.PERMANENT_REDIRECT),

	/**
	 * 인증되지 않았습니다.
	 */
	UNAUTHORIZED("UNAUTHORIZED", "인증되지 않았습니다", HttpStatus.UNAUTHORIZED),

	/**
	 * 결제가 필요합니다.
	 */
	PAYMENT_REQUIRED("PAYMENT_REQUIRED", "결제가 필요합니다", HttpStatus.PAYMENT_REQUIRED),

	/**
	 * 금지된 접근입니다.
	 */
	FORBIDDEN("FORBIDDEN", "금지된 접근입니다", HttpStatus.FORBIDDEN),

	/**
	 * 허용되지 않은 메서드입니다.
	 */
	METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "허용되지 않은 메서드입니다", HttpStatus.METHOD_NOT_ALLOWED),

	/**
	 * 허용되지 않는 요청입니다.
	 */
	NOT_ACCEPTABLE("NOT_ACCEPTABLE", "허용되지 않는 요청입니다", HttpStatus.NOT_ACCEPTABLE),

	/**
	 * 프록시 인증이 필요합니다.
	 */
	PROXY_AUTHENTICATION_REQUIRED("PROXY_AUTHENTICATION_REQUIRED", "프록시 인증이 필요합니다",
		HttpStatus.PROXY_AUTHENTICATION_REQUIRED),

	/**
	 * 요청 시간 초과.
	 */
	REQUEST_TIMEOUT("REQUEST_TIMEOUT", "요청 시간 초과", HttpStatus.REQUEST_TIMEOUT),

	/**
	 * 충돌이 발생했습니다.
	 */
	CONFLICT("CONFLICT", "충돌이 발생했습니다", HttpStatus.CONFLICT),

	/**
	 * 자원이 사라졌습니다.
	 */
	GONE("GONE", "자원이 사라졌습니다", HttpStatus.GONE),

	/**
	 * 길이 필요.
	 */
	LENGTH_REQUIRED("LENGTH_REQUIRED", "길이 필요", HttpStatus.LENGTH_REQUIRED),

	/**
	 * 사전 조건 실패.
	 */
	PRECONDITION_FAILED("PRECONDITION_FAILED", "사전 조건 실패", HttpStatus.PRECONDITION_FAILED),

	/**
	 * 페이로드가 너무 큽니다.
	 */
	PAYLOAD_TOO_LARGE("PAYLOAD_TOO_LARGE", "페이로드가 너무 큽니다", HttpStatus.PAYLOAD_TOO_LARGE),

	/**
	 * URI가 너무 깁니다.
	 */
	URI_TOO_LONG("URI_TOO_LONG", "URI가 너무 깁니다", HttpStatus.URI_TOO_LONG),

	/**
	 * 지원되지 않는 미디어 유형.
	 */
	UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", "지원되지 않는 미디어 유형", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

	/**
	 * 기대 실패.
	 */
	EXPECTATION_FAILED("EXPECTATION_FAILED", "기대 실패", HttpStatus.EXPECTATION_FAILED),

	/**
	 * 처리할 수 없는 엔터티.
	 */
	UNPROCESSABLE_ENTITY("UNPROCESSABLE_ENTITY", "처리할 수 없는 엔터티", HttpStatus.UNPROCESSABLE_ENTITY),

	/**
	 * 잠겨 있습니다.
	 */
	LOCKED("LOCKED", "잠겨 있습니다", HttpStatus.LOCKED),

	/**
	 * 종속성 실패.
	 */
	FAILED_DEPENDENCY("FAILED_DEPENDENCY", "종속성 실패", HttpStatus.FAILED_DEPENDENCY),

	/**
	 * 너무 이른 요청.
	 */
	TOO_EARLY("TOO_EARLY", "너무 이른 요청", HttpStatus.TOO_EARLY),

	/**
	 * 업그레이드가 필요합니다.
	 */
	UPGRADE_REQUIRED("UPGRADE_REQUIRED", "업그레이드가 필요합니다", HttpStatus.UPGRADE_REQUIRED),

	/**
	 * 사전 조건이 필요합니다.
	 */
	PRECONDITION_REQUIRED("PRECONDITION_REQUIRED", "사전 조건이 필요합니다", HttpStatus.PRECONDITION_REQUIRED),

	/**
	 * 요청이 너무 많습니다.
	 */
	TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "요청이 너무 많습니다", HttpStatus.TOO_MANY_REQUESTS),

	/**
	 * 요청 헤더 필드가 너무 큽니다.
	 */
	REQUEST_HEADER_FIELDS_TOO_LARGE("REQUEST_HEADER_FIELDS_TOO_LARGE", "요청 헤더 필드가 너무 큽니다",
		HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE),

	/**
	 * 법적 이유로 사용할 수 없습니다.
	 */
	UNAVAILABLE_FOR_LEGAL_REASONS("UNAVAILABLE_FOR_LEGAL_REASONS", "법적 이유로 사용할 수 없습니다",
		HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS),

	/**
	 * 구현되지 않았습니다.
	 */
	NOT_IMPLEMENTED("NOT_IMPLEMENTED", "구현되지 않았습니다", HttpStatus.NOT_IMPLEMENTED),

	/**
	 * 잘못된 게이트웨이.
	 */
	BAD_GATEWAY("BAD_GATEWAY", "잘못된 게이트웨이", HttpStatus.BAD_GATEWAY),

	/**
	 * 서비스를 사용할 수 없습니다.
	 */
	SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "서비스를 사용할 수 없습니다", HttpStatus.SERVICE_UNAVAILABLE),

	/**
	 * 게이트웨이 시간 초과.
	 */
	GATEWAY_TIMEOUT("GATEWAY_TIMEOUT", "게이트웨이 시간 초과", HttpStatus.GATEWAY_TIMEOUT),

	/**
	 * HTTP 버전이 지원되지 않습니다.
	 */
	HTTP_VERSION_NOT_SUPPORTED("HTTP_VERSION_NOT_SUPPORTED", "HTTP 버전이 지원되지 않습니다",
		HttpStatus.HTTP_VERSION_NOT_SUPPORTED),

	/**
	 * 변형이 협상합니다.
	 */
	VARIANT_ALSO_NEGOTIATES("VARIANT_ALSO_NEGOTIATES", "변형이 협상합니다", HttpStatus.VARIANT_ALSO_NEGOTIATES),

	/**
	 * 저장 공간 부족.
	 */
	INSUFFICIENT_STORAGE("INSUFFICIENT_STORAGE", "저장 공간 부족", HttpStatus.INSUFFICIENT_STORAGE),

	/**
	 * 루프가 감지되었습니다.
	 */
	LOOP_DETECTED("LOOP_DETECTED", "루프가 감지되었습니다", HttpStatus.LOOP_DETECTED),

	/**
	 * 확장되지 않았습니다.
	 */
	NOT_EXTENDED("NOT_EXTENDED", "확장되지 않았습니다", HttpStatus.NOT_EXTENDED),

	// Redis 관련 에러 코드
	REDIS_CONNECTION_ERROR("REDIS001", "Redis 서버에 연결할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	REDIS_OPERATION_ERROR("REDIS002", "Redis 작업이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	REDIS_DATA_NOT_FOUND("REDIS003", "Redis에서 요청된 데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	REDIS_TTL_ERROR("REDIS004", "Redis 키의 TTL을 설정하거나 가져오는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	// 인증 및 권한 관련 에러 코드
	TOKEN_EXPIRED("AUTH002", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	ACCESS_DENIED("AUTH003", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),

	AUTHENTICATION_FAILED("AUTH_001", "Authentication failed", HttpStatus.UNAUTHORIZED),
	INVALID_REDIRECT_URL("AUTH_002", "Invalid or missing Redirect URL", HttpStatus.BAD_REQUEST),

	TOKEN_CREATION_FAILED("1001", "Token creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
	TOKEN_REFRESH_FAILED("1002", "Token refresh failed", HttpStatus.INTERNAL_SERVER_ERROR),
	USER_NOT_FOUND("1005", "User not found", HttpStatus.NOT_FOUND),
	/**
	 * 네트워크 인증이 필요합니다.
	 */
	NETWORK_AUTHENTICATION_REQUIRED("NETWORK_AUTHENTICATION_REQUIRED", "네트워크 인증이 필요합니다",
		HttpStatus.NETWORK_AUTHENTICATION_REQUIRED),
	JWT_TOKEN_MISSING("1006", "JWT token is missing", HttpStatus.BAD_REQUEST),
	JWT_TOKEN_INVALID("1007", "JWT token is invalid", HttpStatus.UNAUTHORIZED),
	KAFKA_SEND_FAILED("KAFKA_SEND_FAILED", "Failed to send log message to Kafka.", HttpStatus.INTERNAL_SERVER_ERROR),
	JWT_TOKEN_EXPIRED("1002", "JWT Token has expired", HttpStatus.UNAUTHORIZED),
	JWT_TOKEN_MALFORMED("1003", "Malformed JWT Token", HttpStatus.BAD_REQUEST),
	CORS_ORIGIN_POLICY_VIOLATION("", "CORS origin policy violation", HttpStatus.FORBIDDEN),
	CORS_METHOD_POLICY_VIOLATION("", "CORS method policy violation", HttpStatus.FORBIDDEN),
	CORS_HEADERS_POLICY_VIOLATION("", "CORS headers policy violation", HttpStatus.FORBIDDEN),

	// XSS 공격 관련 에러 코드
	XSS_DETECTED("", "XSS attack detected", HttpStatus.FORBIDDEN),
	XSS_IN_PARAMETER("", "XSS attack detected in parameter", HttpStatus.FORBIDDEN),
	XSS_IN_COOKIE("", "XSS attack detected in cookie", HttpStatus.FORBIDDEN),
	XSS_IN_BODY("", "XSS attack detected in body", HttpStatus.FORBIDDEN),

	CSRF_TOKEN_INVALID("", "CSRF Token Invalid", HttpStatus.FORBIDDEN);

	private final String code; // 에러 코드
	private final String message; // 에러 메시지
	private final HttpStatus httpStatus; // HTTP 상태 코드
}
