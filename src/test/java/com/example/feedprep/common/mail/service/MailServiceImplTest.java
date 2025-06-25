package com.example.feedprep.common.mail.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import jakarta.mail.Message;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private MailServiceImpl mailService;

    @Test
    @DisplayName("인증 번호는 6자리 수")
    void createAuthNumber() {

        for (int i = 0; i < 100; i++) {
            Long auth = mailService.createAuthNumber();
            assertTrue(auth >= 100000 && auth <= 999999, "인증번호가 6자리가 아님: " + auth);
        }
    }

    @Test
    @DisplayName("메세지 생성 성공")
    void createMail() throws MessagingException, IOException {
        // given
        String email = "testemail@example.com";
        Long authNumber = 123456L;

        MimeMessage message = new MimeMessage((Session) null);

        // when
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        MimeMessage result = mailService.createMail(email,authNumber);

        // then
        assertThat(result.getAllRecipients()[0].toString()).isEqualTo(email);
        assertThat(result.getFrom()[0].toString()).isEqualTo("admin@feedprep.com");
        assertThat(result.getSubject()).isEqualTo("이메일 인증");
        assertThat(result.getContent().toString()).contains(authNumber.toString());
    }

    @Test
    @DisplayName("메세지 전송 성공")
    void sendMail_success() {
        // given
        MimeMessage message = new MimeMessage((Session) null);

        // when
        mailService.sendMail(message);

        // then
        verify(javaMailSender, times(1)).send(message);
    }

    @Test
    @DisplayName("메세지 전송 실패")
    void sendMail_SEND_MAIL_FAIL() {
        // given
        MimeMessage message = new MimeMessage((Session) null);

        // when
        doThrow(new MailSendException("fail")).when(javaMailSender).send(message);

        CustomException exception = assertThrows(CustomException.class,
            () -> mailService.sendMail(message));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SEND_MAIL_FAIL);
    }
}