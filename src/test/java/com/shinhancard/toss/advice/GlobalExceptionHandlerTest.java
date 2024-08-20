package com.shinhancard.toss.advice;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.shinhancard.toss.exception.AuthenticationException;
import com.shinhancard.toss.exception.InvalidRedirectUrlException;
import com.shinhancard.toss.exception.InvalidTokenException;
import com.shinhancard.toss.exception.JwtTokenException;
import com.shinhancard.toss.exception.KafkaException;
import com.shinhancard.toss.exception.RedisConnectionException;
import com.shinhancard.toss.exception.RedisDataNotFoundException;
import com.shinhancard.toss.exception.RedisOperationException;
import com.shinhancard.toss.exception.TokenCreationException;
import com.shinhancard.toss.exception.TokenExpiredException;
import com.shinhancard.toss.exception.TokenRefreshException;
import com.shinhancard.toss.exception.UserNotFoundException;

/**
 * {@link GlobalExceptionHandler}의 예외 처리 기능을 테스트하는 클래스입니다.
 * <p>
 * 각 예외 상황에 대해 적절한 HTTP 상태 코드와 에러 메시지가 반환되는지 확인합니다.
 * </p>
 */
@WebMvcTest
public class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc; // MockMvc 객체를 주입하여 MVC 테스트를 수행합니다.

	@InjectMocks
	private GlobalExceptionHandler globalExceptionHandler; // 테스트할 GlobalExceptionHandler 인스턴스

	@Mock
	private AuthenticationException authenticationException; // AuthenticationException의 Mock 객체

	@Mock
	private InvalidRedirectUrlException invalidRedirectUrlException; // InvalidRedirectUrlException의 Mock 객체

	@Mock
	private InvalidTokenException invalidTokenException; // InvalidTokenException의 Mock 객체

	@Mock
	private JwtTokenException jwtTokenException; // JwtTokenException의 Mock 객체

	@Mock
	private KafkaException kafkaException; // KafkaException의 Mock 객체

	@Mock
	private RedisConnectionException redisConnectionException; // RedisConnectionException의 Mock 객체

	@Mock
	private RedisDataNotFoundException redisDataNotFoundException; // RedisDataNotFoundException의 Mock 객체

	@Mock
	private RedisOperationException redisOperationException; // RedisOperationException의 Mock 객체

	@Mock
	private TokenCreationException tokenCreationException; // TokenCreationException의 Mock 객체

	@Mock
	private TokenExpiredException tokenExpiredException; // TokenExpiredException의 Mock 객체

	@Mock
	private TokenRefreshException tokenRefreshException; // TokenRefreshException의 Mock 객체

	@Mock
	private UserNotFoundException userNotFoundException; // UserNotFoundException의 Mock 객체

	/**
	 * 테스트 실행 전 Mock 객체와 MockMvc를 초기화합니다.
	 */
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this); // Mock 객체 초기화
		mockMvc = MockMvcBuilders.standaloneSetup(globalExceptionHandler).build(); // MockMvc 설정
	}

	/**
	 * AuthenticationException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("AuthenticationException 발생 시 에러 응답 테스트")
	public void testHandleAuthenticationException() throws Exception {
		// Mock 객체의 동작을 정의합니다.
		when(authenticationException.getErrorMessage()).thenReturn("Authentication error"); // 에러 메시지 설정
		when(authenticationException.getHttpStatus()).thenReturn(HttpStatus.UNAUTHORIZED); // HTTP 상태 코드 설정

		mockMvc.perform(get("/test") // 테스트 요청
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized()) // 예상 HTTP 상태 코드 검증
			.andExpect(content().json("{\"error\":\"Authentication error\",\"status\":401}")); // 예상 응답 내용 검증
	}

	/**
	 * InvalidRedirectUrlException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("InvalidRedirectUrlException 발생 시 에러 응답 테스트")
	public void testHandleInvalidRedirectUrlException() throws Exception {
		when(invalidRedirectUrlException.getErrorMessage()).thenReturn("Invalid redirect URL");
		when(invalidRedirectUrlException.getHttpStatus()).thenReturn(HttpStatus.BAD_REQUEST);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().json("{\"error\":\"Invalid redirect URL\",\"status\":400}"));
	}

	/**
	 * InvalidTokenException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("InvalidTokenException 발생 시 에러 응답 테스트")
	public void testHandleInvalidTokenException() throws Exception {
		when(invalidTokenException.getErrorMessage()).thenReturn("Invalid token");
		when(invalidTokenException.getHttpStatus()).thenReturn(HttpStatus.UNAUTHORIZED);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(content().json("{\"error\":\"Invalid token\",\"status\":401}"));
	}

	/**
	 * JwtTokenException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("JwtTokenException 발생 시 에러 응답 테스트")
	public void testHandleJwtTokenException() throws Exception {
		when(jwtTokenException.getErrorMessage()).thenReturn("JWT token error");
		when(jwtTokenException.getHttpStatus()).thenReturn(HttpStatus.UNAUTHORIZED);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(content().json("{\"error\":\"JWT token error\",\"status\":401}"));
	}

	/**
	 * KafkaException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("KafkaException 발생 시 에러 응답 테스트")
	public void testHandleKafkaException() throws Exception {
		when(kafkaException.getErrorMessage()).thenReturn("Kafka error");
		when(kafkaException.getHttpStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andExpect(content().json("{\"error\":\"Kafka error\",\"status\":500}"));
	}

	/**
	 * RedisConnectionException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("RedisConnectionException 발생 시 에러 응답 테스트")
	public void testHandleRedisConnectionException() throws Exception {
		when(redisConnectionException.getErrorMessage()).thenReturn("Redis connection error");
		when(redisConnectionException.getHttpStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andExpect(content().json("{\"error\":\"Redis connection error\",\"status\":500}"));
	}

	/**
	 * RedisDataNotFoundException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("RedisDataNotFoundException 발생 시 에러 응답 테스트")
	public void testHandleRedisDataNotFoundException() throws Exception {
		when(redisDataNotFoundException.getErrorMessage()).thenReturn("Redis data not found");
		when(redisDataNotFoundException.getHttpStatus()).thenReturn(HttpStatus.NOT_FOUND);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(content().json("{\"error\":\"Redis data not found\",\"status\":404}"));
	}

	/**
	 * RedisOperationException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("RedisOperationException 발생 시 에러 응답 테스트")
	public void testHandleRedisOperationException() throws Exception {
		when(redisOperationException.getErrorMessage()).thenReturn("Redis operation error");
		when(redisOperationException.getHttpStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andExpect(content().json("{\"error\":\"Redis operation error\",\"status\":500}"));
	}

	/**
	 * TokenCreationException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("TokenCreationException 발생 시 에러 응답 테스트")
	public void testHandleTokenCreationException() throws Exception {
		when(tokenCreationException.getErrorMessage()).thenReturn("Token creation error");
		when(tokenCreationException.getHttpStatus()).thenReturn(HttpStatus.BAD_REQUEST);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().json("{\"error\":\"Token creation error\",\"status\":400}"));
	}

	/**
	 * TokenExpiredException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("TokenExpiredException 발생 시 에러 응답 테스트")
	public void testHandleTokenExpiredException() throws Exception {
		when(tokenExpiredException.getErrorMessage()).thenReturn("Token expired");
		when(tokenExpiredException.getHttpStatus()).thenReturn(HttpStatus.UNAUTHORIZED);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(content().json("{\"error\":\"Token expired\",\"status\":401}"));
	}

	/**
	 * TokenRefreshException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("TokenRefreshException 발생 시 에러 응답 테스트")
	public void testHandleTokenRefreshException() throws Exception {
		when(tokenRefreshException.getErrorMessage()).thenReturn("Token refresh error");
		when(tokenRefreshException.getHttpStatus()).thenReturn(HttpStatus.BAD_REQUEST);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().json("{\"error\":\"Token refresh error\",\"status\":400}"));
	}

	/**
	 * UserNotFoundException 발생 시 에러 응답을 테스트합니다.
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("UserNotFoundException 발생 시 에러 응답 테스트")
	public void testHandleUserNotFoundException() throws Exception {
		when(userNotFoundException.getErrorMessage()).thenReturn("User not found");
		when(userNotFoundException.getHttpStatus()).thenReturn(HttpStatus.NOT_FOUND);

		mockMvc.perform(get("/test")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(content().json("{\"error\":\"User not found\",\"status\":404}"));
	}

	/**
	 * 일반 예외 발생 시 에러 응답을 테스트합니다.
	 * <p>
	 * 실제 애플리케이션에서 예상치 못한 예외가 발생했을 때의 동작을 테스트합니다.
	 * </p>
	 *
	 * @throws Exception 예외 발생 시
	 */
	@Test
	@DisplayName("일반 예외 발생 시 에러 응답 테스트")
	public void testHandleGeneralException() throws Exception {
		mockMvc.perform(get("/test") // 테스트 요청
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError()) // 예상 HTTP 상태 코드 검증
			.andExpect(content().json("{\"error\":\"예상치 못한 오류가 발생했습니다\",\"status\":500}")); // 예상 응답 내용 검증
	}
}