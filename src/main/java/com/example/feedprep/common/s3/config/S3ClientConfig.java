package com.example.feedprep.common.s3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {

    private final Environment env;

    // AWS S3 버켓의 액세스키, 비밀키 헬퍼 메서드
    private AwsBasicCredentials credentials() {

        return AwsBasicCredentials.create(
            env.getProperty("aws.credentials.access-key"),
            env.getProperty("aws.credentials.secret-key")
        );
    }

    @Bean
    public S3Client amazoneS3() {

        // 접속 지역을 한국(AP_NORTHEAST_2)으로 설정
        return S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(StaticCredentialsProvider.create(credentials()))
            .build();
    }

    @Bean
    public S3Presigner s3Presigner() {

        // Presigned URL 발급을 위한 설정
        return S3Presigner.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(StaticCredentialsProvider.create(credentials()))
            .build();
    }
}
