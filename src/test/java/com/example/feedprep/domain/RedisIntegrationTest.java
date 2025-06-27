package com.example.feedprep.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class RedisIntegrationTest {
	@Container
	static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2.4")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void overrideRedisProps(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", redisContainer::getHost);
		registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
	}

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Test
	void redis_정상작동_확인() {
		redisTemplate.opsForValue().set("paragon", "아르할라");
		String value = redisTemplate.opsForValue().get("paragon");
		assertEquals("아르할라", value);
	}
}
