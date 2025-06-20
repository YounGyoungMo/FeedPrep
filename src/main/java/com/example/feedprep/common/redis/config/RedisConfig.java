package com.example.feedprep.common.redis.config;

import java.nio.charset.StandardCharsets;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Double> ratingTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Double> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());

		template.setValueSerializer(new RedisSerializer<Double>() {
			@Override
			public byte[] serialize(Double value) throws SerializationException {
				return (value == null) ? null : value.toString().getBytes(StandardCharsets.UTF_8);
			}

			@Override
			public Double deserialize(byte[] bytes) throws SerializationException {
				return (bytes == null) ? null : Double.parseDouble(new String(bytes, StandardCharsets.UTF_8));
			}
		});

		template.setHashValueSerializer(new RedisSerializer<Double>() {
			@Override
			public byte[] serialize(Double value) throws SerializationException {
				return (value == null) ? null : value.toString().getBytes(StandardCharsets.UTF_8);
			}

			@Override
			public Double deserialize(byte[] bytes) throws SerializationException {
				return (bytes == null) ? null : Double.parseDouble(new String(bytes, StandardCharsets.UTF_8));
			}
		});

		return template;
	}

	/**
	 *   Long 으로 저장후 Redis 관리는 String으로 하고 꺼내올때 변환 시킬것
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RedisTemplate<String, Long> template(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Long> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());

		RedisSerializer<Long> longSerializer = new RedisSerializer<Long>() {
			@Override
			public byte[] serialize(Long value) throws SerializationException {
				return (value == null) ? null : value.toString().getBytes(StandardCharsets.UTF_8);
			}

			@Override
			public Long deserialize(byte[] bytes) throws SerializationException {
				return (bytes == null) ? null : Long.parseLong(new String(bytes, StandardCharsets.UTF_8));
			}
		};

		template.setValueSerializer(longSerializer);
		template.setHashValueSerializer(longSerializer);

		return template;
	}

	@Bean
	public RedisTemplate<String, Object> dtoTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

		return template;
	}


	@Bean
	public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}
}
