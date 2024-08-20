package com.shinhancard.toss.service;

import java.util.Set;

public interface RedisService {
	void addToSet(String key, String value, long ttlInSeconds); // TTL을 포함한 Set 추가 메서드

	void removeFromSet(String key, String value); // Set에서 값 제거

	boolean isMemberOfSet(String key, String value); // Set에 값이 있는지 확인

	Set<String> getMembersOfSet(String key); // Set의 모든 멤버 조회

	void deleteKey(String key); // 전체 키 삭제

	void setTTL(String key, long ttlInSeconds); // TTL 설정

	long getTTL(String key); // TTL 조회
}
