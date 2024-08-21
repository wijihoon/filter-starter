// package com.shinhancard.toss.config;
//
// import java.time.Duration;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
// import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
// import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.StringRedisSerializer;
//
// import com.shinhancard.toss.properties.RedisProperties;
//
// import lombok.RequiredArgsConstructor;
//
// /**
//  * Redis 설정을 적용하는 구성 클래스입니다.
//  * <p>
//  * 이 클래스는 RedisProperties로부터 Redis 설정을 주입받아 LettuceConnectionFactory와 RedisTemplate을 구성합니다.
//  * </p>
//  */
// @Configuration // Spring의 설정 클래스를 나타냅니다.
// @RequiredArgsConstructor // final 필드에 대해 생성자 주입을 자동으로 생성합니다.
// public class RedisConfig {
//
// 	private final RedisProperties redisProperties;
//
// 	/**
// 	 * Redis 서버와의 연결을 위한 LettuceConnectionFactory를 생성합니다.
// 	 * <p>
// 	 * RedisStandaloneConfiguration 객체를 사용하여 Redis 서버의 호스트와 포트를 설정하고,
// 	 * LettucePoolingClientConfiguration을 통해 커넥션 풀 및 타임아웃을 설정합니다.
// 	 * </p>
// 	 *
// 	 * @return LettuceConnectionFactory Redis와의 연결을 생성하는 팩토리 객체
// 	 */
// 	@Bean
// 	public LettuceConnectionFactory redisConnectionFactory() {
// 		// RedisStandaloneConfiguration 객체를 생성하고 Redis 서버의 호스트와 포트를 설정합니다.
// 		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.getHost(),
// 			redisProperties.getPort());
// 		// Redis 서버의 비밀번호를 설정합니다.
// 		configuration.setPassword(redisProperties.getPassword());
//
// 		// LettucePoolingClientConfiguration을 사용하여 커넥션 풀 및 타임아웃 설정을 구성합니다.
// 		LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
// 			.commandTimeout(Duration.ofMillis(redisProperties.getTimeout())) // 커넥션 타임아웃을 설정합니다.
// 			.build();
//
// 		// LettuceConnectionFactory 객체를 생성하여 Redis 서버와의 연결을 관리합니다.
// 		return new LettuceConnectionFactory(configuration, clientConfig);
// 	}
//
// 	/**
// 	 * RedisTemplate을 설정하여 Redis와의 데이터 상호작용을 처리합니다.
// 	 * <p>
// 	 * RedisTemplate은 Redis와의 데이터 상호작용을 추상화하여 편리하게 사용할 수 있도록 도와줍니다.
// 	 * </p>
// 	 *
// 	 * @param redisConnectionFactory Redis와의 연결을 생성하는 팩토리 객체
// 	 * @return RedisTemplate<String, Object> Redis와의 상호작용을 위한 템플릿 객체
// 	 */
// 	@Bean
// 	public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
// 		// RedisTemplate 객체를 생성합니다.
// 		RedisTemplate<String, Object> template = new RedisTemplate<>();
// 		// 생성된 RedisTemplate 객체에 RedisConnectionFactory를 설정합니다.
// 		template.setConnectionFactory(redisConnectionFactory);
// 		// 키를 직렬화하기 위한 StringRedisSerializer를 설정합니다.
// 		template.setKeySerializer(new StringRedisSerializer());
//
// 		// 값의 직렬화 방식을 JSON으로 설정합니다.
// 		Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
// 		template.setValueSerializer(jsonSerializer);
//
// 		return template; // 설정된 RedisTemplate 객체를 반환합니다.
// 	}
// }