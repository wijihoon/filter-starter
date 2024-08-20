package com.shinhancard.toss.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.shinhancard.toss.properties.RedisProperties;

/**
 * RedisConfig 클래스의 메서드를 테스트하는 클래스입니다.
 * <p>
 * 이 테스트 클래스는 RedisConfig의 redisConnectionFactory와 redisTemplate 메서드를 테스트하여
 * Redis 설정이 올바르게 구성되었는지 확인합니다.
 * </p>
 */
public class RedisConfigTest {

	@Mock // RedisProperties를 모킹합니다.
	private RedisProperties redisProperties;

	@InjectMocks // RedisConfig 인스턴스를 생성하고, @Mock으로 정의된 의존성을 주입합니다.
	private RedisConfig redisConfig;

	@BeforeEach
	public void setUp() {
		// Mockito의 주입 기능을 활성화합니다.
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * RedisConnectionFactory를 생성하는 메서드를 테스트합니다.
	 * <p>
	 * RedisProperties의 설정값을 기반으로 LettuceConnectionFactory가 올바르게 생성되었는지 확인합니다.
	 * </p>
	 */
	@Test
	@DisplayName("redisConnectionFactory should create a valid LettuceConnectionFactory")
	public void testRedisConnectionFactory() {
		// Given: RedisProperties의 설정값을 설정합니다.
		when(redisProperties.getHost()).thenReturn("localhost");
		when(redisProperties.getPort()).thenReturn(6379);
		when(redisProperties.getPassword()).thenReturn("password");
		when(redisProperties.getTimeout()).thenReturn(2000);

		// When: RedisConnectionFactory를 생성합니다.
		LettuceConnectionFactory connectionFactory = redisConfig.redisConnectionFactory();

		// Then: 생성된 LettuceConnectionFactory가 null이 아닌지 확인합니다.
		assertNotNull(connectionFactory, "LettuceConnectionFactory should not be null");

		// 추가적으로 생성된 객체의 설정값을 검증할 수 있습니다.
		assertEquals("localhost", connectionFactory.getStandaloneConfiguration().getHostName());
		assertEquals(6379, connectionFactory.getStandaloneConfiguration().getPort());
		assertEquals("password", connectionFactory.getStandaloneConfiguration().getPassword());
		assertEquals(Duration.ofMillis(2000L), connectionFactory.getClientConfiguration().getCommandTimeout());
	}

	/**
	 * RedisTemplate을 생성하는 메서드를 테스트합니다.
	 * <p>
	 * RedisConnectionFactory를 기반으로 RedisTemplate이 올바르게 설정되었는지 확인합니다.
	 * </p>
	 */
	@Test
	@DisplayName("redisTemplate should create a valid RedisTemplate")
	public void testRedisTemplate() {
		// Given: RedisConnectionFactory를 모킹합니다.
		LettuceConnectionFactory mockConnectionFactory = mock(LettuceConnectionFactory.class);

		// When: RedisTemplate을 생성합니다.
		RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(mockConnectionFactory);

		// Then: 생성된 RedisTemplate이 null이 아닌지 확인합니다.
		assertNotNull(redisTemplate, "RedisTemplate should not be null");

		// RedisTemplate의 설정을 검증합니다.
		assertInstanceOf(StringRedisSerializer.class, redisTemplate.getKeySerializer(),
			"Key serializer should be StringRedisSerializer");
		assertInstanceOf(Jackson2JsonRedisSerializer.class, redisTemplate.getValueSerializer(),
			"Value serializer should be Jackson2JsonRedisSerializer");
	}
}