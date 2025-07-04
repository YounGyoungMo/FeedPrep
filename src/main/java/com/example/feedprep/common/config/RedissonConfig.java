package com.example.feedprep.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port:6379}")
	private int redisPort;

	@Value("${use_secure_redis:true}") // EC2에서는 true로 설정
	private String useSecureRedis;

	@Bean
	public RedissonClient redissonClient() {
		String protocol = "redis://";
		if(useSecureRedis.equals("true")){
			protocol = "rediss://";
		}
		String address = protocol + redisHost + ":" + redisPort;

		Config config = new Config();
		config.useSingleServer()
			.setAddress(address)
			.setConnectionMinimumIdleSize(1)
			.setConnectionPoolSize(10);

		return Redisson.create(config);
	}
}
