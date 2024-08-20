package com.shinhancard.toss.service.impl;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.shinhancard.toss.exception.ErrorCode;
import com.shinhancard.toss.exception.RedisConnectionException;
import com.shinhancard.toss.exception.RedisDataNotFoundException;
import com.shinhancard.toss.exception.RedisOperationException;
import com.shinhancard.toss.service.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis 서비스 구현체입니다.
 * <p>
 * Redis 데이터베이스와 상호작용하여 집합(Set) 데이터 구조를 관리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

	private final RedisTemplate<String, String> redisTemplate; // RedisTemplate 주입

	/**
	 * 지정된 키의 집합에 값을 추가하고, TTL(생존 시간)을 설정합니다.
	 *
	 * @param key          Redis 집합의 키
	 * @param value        Redis 집합에 추가할 값
	 * @param ttlInSeconds 값의 TTL(초 단위)
	 * @throws RedisConnectionException Redis 연결 오류 발생 시
	 * @throws RedisOperationException  Redis 데이터 접근 오류 발생 시
	 */
	@Override
	public void addToSet(String key, String value, long ttlInSeconds) {
		try {
			redisTemplate.opsForSet().add(key, value); // Redis 집합에 값 추가
			setTTL(key, ttlInSeconds); // TTL 설정
		} catch (RedisConnectionFailureException e) {
			handleRedisConnectionException(e); // Redis 연결 오류 처리
		} catch (DataAccessException e) {
			handleRedisOperationException(e); // Redis 데이터 접근 오류 처리
		}
	}

	/**
	 * 지정된 키의 집합에서 값을 제거합니다.
	 *
	 * @param key   Redis 집합의 키
	 * @param value Redis 집합에서 제거할 값
	 * @throws RedisConnectionException   Redis 연결 오류 발생 시
	 * @throws RedisDataNotFoundException 값이 집합에 존재하지 않는 경우
	 * @throws RedisOperationException    Redis 데이터 접근 오류 발생 시
	 */
	@Override
	public void removeFromSet(String key, String value) {
		try {
			Long removed = redisTemplate.opsForSet().remove(key, value); // Redis 집합에서 값 제거
			if (removed == null || removed == 0) {
				throw new RedisDataNotFoundException(ErrorCode.REDIS_DATA_NOT_FOUND); // 값이 존재하지 않을 경우 예외 발생
			}
		} catch (RedisConnectionFailureException e) {
			handleRedisConnectionException(e); // Redis 연결 오류 처리
		} catch (DataAccessException e) {
			handleRedisOperationException(e); // Redis 데이터 접근 오류 처리
		}
	}

	/**
	 * 지정된 키의 집합에 값이 존재하는지 확인합니다.
	 *
	 * @param key   Redis 집합의 키
	 * @param value Redis 집합에서 확인할 값
	 * @return 값이 집합에 존재하면 true, 그렇지 않으면 false
	 * @throws RedisConnectionException Redis 연결 오류 발생 시
	 * @throws RedisOperationException  Redis 데이터 접근 오류 발생 시
	 */
	@Override
	public boolean isMemberOfSet(String key, String value) {
		try {
			Boolean isMember = redisTemplate.opsForSet().isMember(key, value); // 값이 집합에 존재하는지 확인
			if (isMember == null) {
				throw new RedisOperationException(ErrorCode.REDIS_OPERATION_ERROR); // 오류 발생 시 예외 처리
			}
			return isMember;
		} catch (RedisConnectionFailureException e) {
			handleRedisConnectionException(e); // Redis 연결 오류 처리
		} catch (DataAccessException e) {
			handleRedisOperationException(e); // Redis 데이터 접근 오류 처리
		}
		return false; // 기본값으로 false 반환
	}

	/**
	 * 지정된 키의 집합에서 모든 멤버를 가져옵니다.
	 *
	 * @param key Redis 집합의 키
	 * @return 집합의 모든 멤버
	 * @throws RedisConnectionException   Redis 연결 오류 발생 시
	 * @throws RedisDataNotFoundException 집합이 비어있거나 존재하지 않는 경우
	 * @throws RedisOperationException    Redis 데이터 접근 오류 발생 시
	 */
	@Override
	public Set<String> getMembersOfSet(String key) {
		try {
			Set<String> members = redisTemplate.opsForSet().members(key); // 집합의 모든 멤버 가져오기
			if (members == null || members.isEmpty()) {
				throw new RedisDataNotFoundException(ErrorCode.REDIS_DATA_NOT_FOUND); // 멤버가 없을 경우 예외 발생
			}
			return members;
		} catch (RedisConnectionFailureException e) {
			handleRedisConnectionException(e); // Redis 연결 오류 처리
		} catch (DataAccessException e) {
			handleRedisOperationException(e); // Redis 데이터 접근 오류 처리
		}
		return Set.of(); // 기본값으로 빈 집합 반환
	}

	/**
	 * 지정된 키의 값을 삭제합니다.
	 *
	 * @param key Redis 키
	 * @throws RedisConnectionException   Redis 연결 오류 발생 시
	 * @throws RedisDataNotFoundException 키가 존재하지 않을 경우
	 * @throws RedisOperationException    Redis 데이터 접근 오류 발생 시
	 */
	@Override
	public void deleteKey(String key) {
		try {
			Boolean deleted = redisTemplate.delete(key); // Redis에서 키 삭제
			if (deleted == null || !deleted) {
				throw new RedisDataNotFoundException(ErrorCode.REDIS_DATA_NOT_FOUND); // 키가 존재하지 않을 경우 예외 발생
			}
		} catch (RedisConnectionFailureException e) {
			handleRedisConnectionException(e); // Redis 연결 오류 처리
		} catch (DataAccessException e) {
			handleRedisOperationException(e); // Redis 데이터 접근 오류 처리
		}
	}

	/**
	 * 지정된 키의 TTL(생존 시간)을 설정합니다.
	 *
	 * @param key          Redis 키
	 * @param ttlInSeconds TTL(초 단위)
	 * @throws RedisConnectionException Redis 연결 오류 발생 시
	 * @throws RedisOperationException  TTL 설정 오류 발생 시
	 */
	@Override
	public void setTTL(String key, long ttlInSeconds) {
		try {
			Boolean success = redisTemplate.expire(key, ttlInSeconds, TimeUnit.SECONDS); // TTL 설정
			if (success == null || !success) {
				throw new RedisOperationException(ErrorCode.REDIS_TTL_ERROR); // TTL 설정 실패 시 예외 발생
			}
		} catch (RedisConnectionFailureException e) {
			handleRedisConnectionException(e); // Redis 연결 오류 처리
		} catch (DataAccessException e) {
			handleRedisOperationException(e); // Redis 데이터 접근 오류 처리
		}
	}

	/**
	 * 지정된 키의 TTL(생존 시간)을 가져옵니다.
	 *
	 * @param key Redis 키
	 * @return TTL(초 단위)
	 * @throws RedisConnectionException Redis 연결 오류 발생 시
	 * @throws RedisOperationException  TTL 조회 오류 발생 시
	 */
	@Override
	public long getTTL(String key) {
		try {
			Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS); // TTL 조회
			if (ttl == null) {
				throw new RedisOperationException(ErrorCode.REDIS_TTL_ERROR); // TTL 조회 실패 시 예외 발생
			}
			return ttl;
		} catch (RedisConnectionFailureException e) {
			handleRedisConnectionException(e); // Redis 연결 오류 처리
		} catch (DataAccessException e) {
			handleRedisOperationException(e); // Redis 데이터 접근 오류 처리
		}
		return -1; // 기본값으로 -1 반환
	}

	/**
	 * Redis 연결 오류를 처리합니다.
	 *
	 * @param e Redis 연결 실패 예외
	 * @throws RedisConnectionException Redis 연결 오류 예외
	 */
	private void handleRedisConnectionException(RedisConnectionFailureException e) {
		log.error("Redis connection failure", e);
		throw new RedisConnectionException(ErrorCode.REDIS_CONNECTION_ERROR);
	}

	/**
	 * Redis 데이터 접근 오류를 처리합니다.
	 *
	 * @param e Redis 데이터 접근 예외
	 * @throws RedisOperationException Redis 데이터 접근 오류 예외
	 */
	private void handleRedisOperationException(DataAccessException e) {
		log.error("Redis operation failure", e);
		throw new RedisOperationException(ErrorCode.REDIS_OPERATION_ERROR);
	}
}