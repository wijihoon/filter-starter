package com.shinhancard.toss.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.exception.InvalidRedirectUrlException;
import com.shinhancard.toss.exception.InvalidTokenException;
import com.shinhancard.toss.exception.JwtTokenException;
import com.shinhancard.toss.exception.KafkaException;
import com.shinhancard.toss.exception.TokenCreationException;
import com.shinhancard.toss.exception.TokenExpiredException;
import com.shinhancard.toss.exception.TokenRefreshException;
import com.shinhancard.toss.exception.UserNotFoundException;
import com.shinhancard.toss.io.ResponseVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하여 적절한 HTTP 응답을 생성하는 클래스입니다.
 * <p>
 * 이 클래스는 다양한 예외를 잡아서 사용자에게 적절한 에러 응답을 반환합니다.
 * </p>
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * AuthenticationException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * AuthenticationException의 ErrorCode에 따라 적절한 응답을 생성합니다.
	 * </p>
	 *
	 * @param e 발생한 AuthenticationException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ResponseVo<Object>> handleAuthenticationException(AuthenticationException e) {
		// AuthenticationException 발생 시 로그 기록
		log.error("AuthenticationException 발생: 코드={}, 메시지={}", e.getErrorCode(), e.getErrorMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * InvalidRedirectUrlException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * 유효하지 않은 리디렉션 URL에 대한 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 InvalidRedirectUrlException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(InvalidRedirectUrlException.class)
	public ResponseEntity<ResponseVo<Object>> handleInvalidRedirectUrlException(InvalidRedirectUrlException e) {
		// InvalidRedirectUrlException 발생 시 로그 기록
		log.error("InvalidRedirectUrlException 발생: 메시지={}", e.getMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * InvalidTokenException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * 유효하지 않은 토큰에 대한 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 InvalidTokenException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ResponseVo<Object>> handleInvalidTokenException(InvalidTokenException e) {
		// InvalidTokenException 발생 시 로그 기록
		log.error("InvalidTokenException 발생: 메시지={}", e.getMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * JwtTokenException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * JWT 토큰 관련 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 JwtTokenException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(JwtTokenException.class)
	public ResponseEntity<ResponseVo<Object>> handleJwtTokenException(JwtTokenException e) {
		// JwtTokenException 발생 시 로그 기록
		log.error("JwtTokenException 발생: 메시지={}", e.getMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * KafkaException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * Kafka 관련 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 KafkaException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(KafkaException.class)
	public ResponseEntity<ResponseVo<Object>> handleKafkaException(KafkaException e) {
		// KafkaException 발생 시 로그 기록
		log.error("KafkaException 발생: 코드={}, 메시지={}", e.getErrorCode(), e.getErrorMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * TokenCreationException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * 토큰 생성 관련 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 TokenCreationException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(TokenCreationException.class)
	public ResponseEntity<ResponseVo<Object>> handleTokenCreationException(TokenCreationException e) {
		// TokenCreationException 발생 시 로그 기록
		log.error("TokenCreationException 발생: 메시지={}", e.getMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * TokenExpiredException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * 토큰 만료 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 TokenExpiredException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<ResponseVo<Object>> handleTokenExpiredException(TokenExpiredException e) {
		// TokenExpiredException 발생 시 로그 기록
		log.error("TokenExpiredException 발생: 메시지={}", e.getMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * TokenRefreshException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * 토큰 갱신 관련 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 TokenRefreshException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(TokenRefreshException.class)
	public ResponseEntity<ResponseVo<Object>> handleTokenRefreshException(TokenRefreshException e) {
		// TokenRefreshException 발생 시 로그 기록
		log.error("TokenRefreshException 발생: 메시지={}", e.getMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * UserNotFoundException 발생 시 호출되는 메소드입니다.
	 * <p>
	 * 사용자 정보가 없을 때의 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 UserNotFoundException 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseVo<Object>> handleUserNotFoundException(UserNotFoundException e) {
		// UserNotFoundException 발생 시 로그 기록
		log.error("UserNotFoundException 발생: 메시지={}", e.getMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse(e.getErrorMessage(), e.getHttpStatus());
	}

	/**
	 * 기타 모든 예외를 처리합니다.
	 * <p>
	 * 예상치 못한 에러를 처리합니다.
	 * </p>
	 *
	 * @param e 발생한 예외 객체
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseVo<Object>> handleGeneralException(Exception e) {
		// 일반 예외 발생 시 로그 기록
		log.error("예외 발생: 메시지={}", e.getMessage(), e);
		// 에러 응답 반환
		return buildErrorResponse("예상치 못한 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 에러 응답을 생성하는 공통 메소드입니다.
	 *
	 * @param message 에러 메시지
	 * @param status  HTTP 상태 코드
	 * @return 에러 응답을 담은 ResponseEntity 객체
	 */
	private ResponseEntity<ResponseVo<Object>> buildErrorResponse(String message, HttpStatus status) {
		// ResponseVo 객체를 사용하여 에러 응답 생성 및 반환
		return ResponseEntity.status(status).body(ResponseVo.error(message, status));
	}
}
