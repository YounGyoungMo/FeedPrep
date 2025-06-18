package com.example.feedprep.common.redis.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthNumberRedisServiceImpl implements AuthNumberRedisService{

    private final RedisTemplate<String, Long> template;

    public void saveAuthNumber(String email, Long authNumber) {
        template.opsForValue().set(email, authNumber, 5, TimeUnit.MINUTES);
    }

    public Long getAuthNumber(String email) {
        return template.opsForValue().get(email);
    }

    public void deleteAuthNumber(String email) {
        template.delete(email);
    }
}
