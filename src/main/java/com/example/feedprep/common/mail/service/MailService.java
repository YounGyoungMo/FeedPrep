package com.example.feedprep.common.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface MailService {

    Long createAuthNumber();

    MimeMessage createMail(String mail, Long AuthNumber) throws MessagingException;

    Void sendMail(MimeMessage message);
}
