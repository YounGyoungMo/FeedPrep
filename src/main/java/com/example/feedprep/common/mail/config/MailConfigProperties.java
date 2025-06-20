package com.example.feedprep.common.mail.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailConfigProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
