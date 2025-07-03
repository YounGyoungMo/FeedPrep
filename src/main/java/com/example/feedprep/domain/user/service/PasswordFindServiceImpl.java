package com.example.feedprep.domain.user.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.mail.service.MailService;
import com.example.feedprep.common.redis.service.AuthNumberRedisService;
import com.example.feedprep.domain.user.dto.request.MailAuthRequestDto;
import com.example.feedprep.domain.user.dto.request.MailRequestDto;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordFindServiceImpl implements PasswordFindService{

    private final UserRepository userRepository;
    private final MailService mailService;
    private final AuthNumberRedisService authNumberRedisService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void sendMail(MailRequestDto mailRequestDto) {

        // 회원 확인 여부
        User user = userRepository.getUserByEmailOrElseThrow(mailRequestDto.getEmail());

        // 인증 번호 발급
        Long authNumber = mailService.createAuthNumber();

        // 메일 생성
        MimeMessage mail;
        try {
            mail = mailService.createMail(user.getEmail(), authNumber);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.CREATE_MAIL_FAIL);
        }

        // redis에 인증번호 저장 - 인증번호는 5분까지 유효
        authNumberRedisService.saveAuthNumber(user.getEmail(), authNumber);

        // 이메일 전송 코드
        mailService.sendMail(mail);
    }

    @Override
    @Transactional
    public void changeLostPassword(MailAuthRequestDto mailAuthRequestDto) {

        // 회원 확인 여부
        User user = userRepository.getUserByEmailOrElseThrow(mailAuthRequestDto.getEmail());

        // 인증 확인 여부 - redis
        Long authNumber = authNumberRedisService.getAuthNumber(user.getEmail());

        Long ReceivedNumber = Long.valueOf(mailAuthRequestDto.getAuthNumber());

        if(authNumber == null || !authNumber.equals(ReceivedNumber)) {
            throw new CustomException(ErrorCode.NOT_CONFIRMED_AUTHNUMBER);
        }

        // 비밀번호 변경
        String encodePassword = passwordEncoder.encode(mailAuthRequestDto.getNewPassword());

        user.setPassword(encodePassword);

        // 인증된 번호삭제
        authNumberRedisService.deleteAuthNumber(user.getEmail());
    }
}
