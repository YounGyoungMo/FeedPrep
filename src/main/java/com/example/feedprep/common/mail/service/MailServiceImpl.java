package com.example.feedprep.common.mail.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.mail.config.MailConfigProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final MailConfigProperties mailProps;

    @Override
    public Long createAuthNumber() {

        return (long) (Math.random()*900000)+100000;
    }

    @Override
    public MimeMessage createMail(String mail, Long AuthNumber) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(mailProps.getUsername());
        message.setRecipients(RecipientType.TO, mail);
        message.setSubject("이메일 인증");

        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + AuthNumber + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    @Override
    public void sendMail(MimeMessage message) {

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new CustomException(ErrorCode.SEND_MAIL_FAIL);
        }
    }
}
