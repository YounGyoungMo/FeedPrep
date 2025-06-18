package com.example.feedprep.domain.user.service;

import com.example.feedprep.domain.user.dto.request.MailAuthRequestDto;
import com.example.feedprep.domain.user.dto.request.MailRequestDto;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface PasswordFindService {

    void sendMail(@Valid MailRequestDto mailRequestDto) throws MessagingException;

    void changeLostPassword(@Valid MailAuthRequestDto mailAuthRequestDto);
}
