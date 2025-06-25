package com.example.feedprep.common.redis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthNumberRedisServiceImplTest {

    @Mock
    private RedisTemplate<String, Long> template;

    @Mock
    private ValueOperations<String, Long> valueOperations;

    @InjectMocks
    private AuthNumberRedisServiceImpl authNumberRedisService;

    @Test
    @DisplayName("인증번호 저장")
    void saveAuthNumber() {
        // given
        String email = "testemail@example.com";
        Long authNumber = 123456L;

        // when
        when(template.opsForValue()).thenReturn(valueOperations);

        authNumberRedisService.saveAuthNumber(email,authNumber);

        // then
        verify(valueOperations, times(1)).set(email,authNumber,5, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("인증번호 조회")
    void getAuthNumber() {
        // given
        String email = "testemail@example.com";
        Long authNumber = 123456L;

        // when
        when(template.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(authNumber);

        Long result = authNumberRedisService.getAuthNumber(email);

        // then
        assertThat(result).isEqualTo(authNumber);
        verify(valueOperations, times(1)).get(email);
    }

    @Test
    @DisplayName("인증번호 삭제")
    void deleteAuthNumber() {
        // given
        String email = "testemail@example.com";

        // when
        authNumberRedisService.deleteAuthNumber(email);

        // then
        verify(template, times(1)).delete(email);
    }
}