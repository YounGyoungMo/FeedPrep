package com.example.feedprep.common.web.config;

import io.portone.sdk.server.common.Country;

import com.example.feedprep.common.web.argument.AuthUserArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${EC2_URL:http://localhost}")
    private String webHost;

    @Value("${EC2_PORT:8080}")
    private String webPort;

    private final AuthUserArgumentResolver authUserArgumentResolver;

    public WebConfig(AuthUserArgumentResolver authUserArgumentResolver) {
        this.authUserArgumentResolver = authUserArgumentResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String localOrigin = "http://127.0.0.1:5500"; // 프론트 로컬 주소
        String setOrigin;
        if (StringUtils.hasText(webHost) && StringUtils.hasText(webPort)) {
            setOrigin = webHost + ":" + webPort;
        } else {
            setOrigin = "http://localhost:5000";
        }

        registry.addMapping("/**")
            .allowedOrigins(setOrigin, localOrigin)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Authorization")
            .allowCredentials(true);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authUserArgumentResolver);
    }
}
