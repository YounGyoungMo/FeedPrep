package com.example.feedprep.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailRequestDto {

    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
}
