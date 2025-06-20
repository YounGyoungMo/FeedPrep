package com.example.feedprep.domain.user.controller;


import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.user.dto.request.MailAuthRequestDto;
import com.example.feedprep.domain.user.dto.request.MailRequestDto;
import com.example.feedprep.domain.user.service.PasswordFindService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/find")
@RequiredArgsConstructor
public class PasswordFindController {

    private final PasswordFindService passwordFindService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> sendMail(
        @Valid @RequestBody MailRequestDto mailRequestDto
    ) throws MessagingException {
        passwordFindService.sendMail(mailRequestDto);

        return ResponseEntity.status(SuccessCode.SEND_MAIL_SUCCESS.getHttpStatus())
            .body(ApiResponseDto.success(SuccessCode.SEND_MAIL_SUCCESS));
    }

    @PatchMapping
    public ResponseEntity<ApiResponseDto<Void>> changeLostPassword(
        @Valid @RequestBody MailAuthRequestDto mailAuthRequestDto
    ) {
        passwordFindService.changeLostPassword(mailAuthRequestDto);

        return ResponseEntity.status(SuccessCode.CHANGE_PASSWORD_SUCCESS.getHttpStatus())
            .body(ApiResponseDto.success(SuccessCode.CHANGE_PASSWORD_SUCCESS));
    }
}
