package com.shinhancard.toss.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import com.shinhancard.toss.service.impl.RedisServiceImpl;

/**
 * RedisServiceImpl의 기능을 테스트하기 위한 단위 테스트 클래스입니다.
 */
class RedisServiceTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate; // RedisTemplate Mock 객체

	@InjectMocks
	private RedisServiceImpl redisService; // 테스트할 RedisServiceImpl 객체

	/**
	 * 테스트 실행 전에 Mock 객체를 초기화합니다.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Mockito의 Mock 객체를 초기화
	}

	/**
	 * Redis 집합에 값을 추가하고 TTL을 설정하는 테스트입니다.
	 */
	@Test
	@DisplayName("Redis 집합에 값을 추가하고 TTL을 설정하는 테스트")
	void testAddToSet() {
		String key = "mySet";
		String value = "value1";
		long ttl = 3600; // TTL 설정

		// Mock 설정
		when(redisTemplate.opsForSet().add(key, value)).thenReturn(1L); // 값 추가 성공
		when(redisTemplate.expire(key, ttl, TimeUnit.SECONDS)).thenReturn(true); // TTL 설정 성공

		// 메서드 호출
		redisService.addToSet(key, value, ttl);

		// 검증
		verify(redisTemplate).opsForSet().add(key, value); // 값 추가 호출 검증
		verify(redisTemplate).expire(key, ttl, TimeUnit.SECONDS); // TTL 설정 호출 검증
	}

	/**
	 * Redis 집합에서 값을 제거하는 테스트입니다.
	 */
	@Test
	@DisplayName("Redis 집합에서 값을 제거하는 테스트")
	void testRemoveFromSet() {
		String key = "mySet";
		String value = "value1";

		// Mock 설정
		when(redisTemplate.opsForSet().remove(key, value)).thenReturn(1L); // 값 제거 성공

		// 메서드 호출
		redisService.removeFromSet(key, value);

		// 검증
		verify(redisTemplate).opsForSet().remove(key, value); // 값 제거 호출 검증
	}

	/**
	 * Redis 집합의 멤버를 확인하는 테스트입니다.
	 */
	@Test
	@DisplayName("Redis 집합의 멤버를 확인하는 테스트")
	void testIsMemberOfSet() {
		String key = "mySet";
		String value = "value1";

		// Mock 설정
		when(redisTemplate.opsForSet().isMember(key, value)).thenReturn(true); // 값이 집합에 존재함

		// 메서드 호출 및 검증
		assertTrue(redisService.isMemberOfSet(key, value)); // 값 존재 확인
	}

	/**
	 * Redis 집합의 모든 멤버를 가져오는 테스트입니다.
	 */
	@Test
	@DisplayName("Redis 집합의 모든 멤버를 가져오는 테스트")
	void testGetMembersOfSet() {
		String key = "mySet";
		Set<String> members = new HashSet<>();
		members.add("value1");
		members.add("value2");

		// Mock 설정
		when(redisTemplate.opsForSet().members(key)).thenReturn(members); // 멤버 가져오기 성공

		// 메서드 호출 및 검증
		assertEquals(members, redisService.getMembersOfSet(key)); // 멤버 목록 검증
	}

	/**
	 * Redis 키를 삭제하는 테스트입니다.
	 */
	@Test
	@DisplayName("Redis 키를 삭제하는 테스트")
	void testDeleteKey() {
		String key = "myKey";

		// Mock 설정
		when(redisTemplate.delete(key)).thenReturn(true); // 키 삭제 성공

		// 메서드 호출
		redisService.deleteKey(key);

		// 검증
		verify(redisTemplate).delete(key); // 키 삭제 호출 검증
	}

	/**
	 * Redis 키의 TTL을 설정하는 테스트입니다.
	 */
	@Test
	@DisplayName("Redis 키의 TTL을 설정하는 테스트")
	void testSetTTL() {
		String key = "myKey";
		long ttl = 3600; // TTL 설정

		// Mock 설정
		when(redisTemplate.expire(key, ttl, TimeUnit.SECONDS)).thenReturn(true); // TTL 설정 성공

		// 메서드 호출
		redisService.setTTL(key, ttl);

		// 검증
		verify(redisTemplate).expire(key, ttl, TimeUnit.SECONDS); // TTL 설정 호출 검증
	}

	/**
	 * Redis 키의 TTL을 가져오는 테스트입니다.
	 */
	@Test
	@DisplayName("Redis 키의 TTL을 가져오는 테스트")
	void testGetTTL() {
		String key = "myKey";
		long ttl = 3600; // TTL 값

		// Mock 설정
		when(redisTemplate.getExpire(key, TimeUnit.SECONDS)).thenReturn(ttl); // TTL 조회 성공

		// 메서드 호출 및 검증
		assertEquals(ttl, redisService.getTTL(key)); // TTL 값 검증
	}
}
