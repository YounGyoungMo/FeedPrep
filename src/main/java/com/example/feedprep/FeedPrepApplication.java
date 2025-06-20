package com.example.feedprep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
// @EnableCaching
@EnableAsync // 비동기 어노테이션
public class FeedPrepApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedPrepApplication.class, args);
    }
}
