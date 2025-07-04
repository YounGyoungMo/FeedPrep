package com.example.feedprep.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.mail.service.MailService;
import com.example.feedprep.common.redis.service.AuthNumberRedisService;
import com.example.feedprep.domain.user.dto.request.MailAuthRequestDto;
import com.example.feedprep.domain.user.dto.request.MailRequestDto;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PasswordFindServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @Mock
    private AuthNumberRedisService authNumberRedisService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordFindServiceImpl passwordFindService;

    @Test
    @DisplayName("인증번호 발송")
    void sendMail_success() throws MessagingException {
        // given
        String email = "testemail@example.com";
        Long authNumber = 123456L;
        User user = User.builder().userId(1L).email(email).build();

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        MailRequestDto mailRequestDto = new MailRequestDto(email);

        // when
        when(userRepository.getUserByEmailOrElseThrow(email)).thenReturn(user);
        when(mailService.createAuthNumber()).thenReturn(authNumber);
        when(mailService.createMail(user.getEmail(), authNumber)).thenReturn(message);

        passwordFindService.sendMail(mailRequestDto);

        // then
        verify(authNumberRedisService, times(1)).saveAuthNumber(user.getEmail(), authNumber);
        verify(mailService, times(1)).sendMail(message);
    }

    @Test
    @DisplayName("메일 생성 실패")
    void sendMail_CREATE_MAIL_FAIL() throws MessagingException {
        // given
        String email = "testemail@example.com";
        Long authNumber = 123456L;
        User user = User.builder().userId(1L).email(email).build();

        MailRequestDto mailRequestDto = new MailRequestDto(email);

        // when
        when(userRepository.getUserByEmailOrElseThrow(email)).thenReturn(user);
        when(mailService.createAuthNumber()).thenReturn(authNumber);

        doThrow(new MessagingException("메일 생성 실패")).when(mailService).createMail(email,authNumber);

        CustomException exception = assertThrows(CustomException.class,
            () -> passwordFindService.sendMail(mailRequestDto)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CREATE_MAIL_FAIL);
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changeLostPassword_success() {
        // given
        String email = "testemail@example.com";
        String ReceivedNumber ="123456";
        Long authNumber = 123456L;
        User user = User.builder().userId(1L).email(email).build();

        MailAuthRequestDto mailAuthRequestDto =
            new MailAuthRequestDto(email, ReceivedNumber, "newPassword1234");

        // when
        when(userRepository.getUserByEmailOrElseThrow(email)).thenReturn(user);
        when(authNumberRedisService.getAuthNumber(email)).thenReturn(authNumber);
        when(passwordEncoder.encode("newPassword1234")).thenReturn("encoidedPassword");

        passwordFindService.changeLostPassword(mailAuthRequestDto);

        // then
        assertThat(user.getPassword()).isEqualTo("encoidedPassword");
        verify(authNumberRedisService, times(1)).deleteAuthNumber(user.getEmail());
    }

    @Test
    @DisplayName("인증번호가 만료 되거나, 일치하지 않음")
    void changeLostPassword_NOT_CONFIRMED_AUTHNUMBER() {
        // given
        String email = "testemail@example.com";
        User user = User.builder().userId(1L).email(email).build();

        MailAuthRequestDto mailAuthRequestDto =
            new MailAuthRequestDto(email, "123456", null);

        // when
        when(userRepository.getUserByEmailOrElseThrow(email)).thenReturn(user);
        when(authNumberRedisService.getAuthNumber(email)).thenReturn(654321L);

        CustomException exception = assertThrows(CustomException.class,
            () -> passwordFindService.changeLostPassword(mailAuthRequestDto)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_CONFIRMED_AUTHNUMBER);
    }
}