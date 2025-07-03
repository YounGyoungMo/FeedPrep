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

	private  String setAddress;
	@Bean
	public RedissonClient redissonClient(){
		setAddress ="redis://" + redisHost + ":6379";
		Config config = new Config();
		config.useSingleServer()
			.setAddress(setAddress)
			.setConnectionMinimumIdleSize(1)
			.setConnectionPoolSize(10);
		return Redisson.create(config);
	}
}
