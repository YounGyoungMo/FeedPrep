package com.example.feedprep.common.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.example.feedprep.common.constants.BusinessRuleConstants.BLACKLIST_PREFIX;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenBlacklistService {
    private final RedisTemplate<String,String> redisTemplate;

        /**
         * 토큰을 블랙리스트에 등록, 만료시간까지 보관
         * @param token 토큰 문자열
         * @param expirationMillis 남은 만료 시간 (ms)
         */
        public void addTokenToBlacklist(String token, long expirationMillis) {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "logout", expirationMillis, TimeUnit.MILLISECONDS);
        }

    /**
     * 토큰이 블랙리스트에 존재하는지 확인
     * @param token 토큰 문자열
     * @return true면 블랙리스트에 등록된 상태
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }





}
