package com.example.feedprep.common.config;

import com.example.feedprep.common.redis.config.RedisConfig;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration config = RedisCacheConfiguration
            .defaultCacheConfig()
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext
                .SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));// key 직렬화 방식 지정 (문자열)

        //저장할 캐시의 설정 구조
        Map<String, RedisCacheConfiguration> redisCacheConfigMap
            = new HashMap<>();

        // redisCacheConfigMap.put("key", defaultConfig.entryTtl(Duration.ofHours(4)) //특정 키에 TTL 시간을 자동으로 부착
        // 	.disableCachingNullValues());

        return RedisCacheManager.builder(redisConnectionFactory)
            .withInitialCacheConfigurations(redisCacheConfigMap)
            .build();
    }
    @Bean
    @Primary// JVM 기반 캐시사용, CacheManager를 명시하지 않으면 이걸 기본으로 사용.
    public RedisConfig cacheManager() {
        //return  new ConcurrentMapCacheManager("key");
        return null;
    }
}
