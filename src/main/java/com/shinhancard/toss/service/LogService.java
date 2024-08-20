package com.shinhancard.toss.service;

/**
 * 로그를 전송하는 서비스 인터페이스입니다.
 */
public interface LogService {

	/**
	 * 로그 데이터를 전송합니다.
	 *
	 * @param logJson 로그 데이터를 JSON 형식으로 변환한 문자열
	 */
	void sendLog(String logJson);
}
