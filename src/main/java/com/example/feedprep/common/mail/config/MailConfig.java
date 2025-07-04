package com.example.feedprep.common.mail.config;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailConfigProperties mailProps;

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProps.getHost());
        mailSender.setPort(mailProps.getPort());
        mailSender.setUsername(mailProps.getUsername());
        mailSender.setPassword(mailProps.getPassword());

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.starttls.required", true);
        props.put("mail.smtp.ssl.enable", false);

        mailSender.setJavaMailProperties(props);

        return mailSender;
    }
}
