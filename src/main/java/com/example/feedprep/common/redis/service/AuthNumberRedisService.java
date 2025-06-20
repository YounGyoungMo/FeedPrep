package com.example.feedprep.common.redis.service;

public interface AuthNumberRedisService {

    // 인증번호 저장 5분만
    void saveAuthNumber(String email, Long authNumber);

    // 비교를 위해 인증번호 조회
    Long getAuthNumber(String email);

    // 인증번호 삭제
    void deleteAuthNumber(String email);
}
