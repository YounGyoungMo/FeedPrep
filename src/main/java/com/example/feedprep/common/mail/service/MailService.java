package com.example.feedprep.common.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface MailService {

    // 6자리 인증번호 발급
    Long createAuthNumber();

    // 메세지 형식 작성
    MimeMessage createMail(String mail, Long AuthNumber) throws MessagingException;

    // 메세지 전송
    Void sendMail(MimeMessage message);
}
