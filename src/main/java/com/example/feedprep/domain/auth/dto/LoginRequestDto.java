package com.example.feedprep.domain.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;

@Getter
@RequiredArgsConstructor
public class LoginRequestDto {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private final String email;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "비밀번호는 소문자, 대문자, 숫자를 포함한 8자 이상이어야 합니다."
    )
    private final String password;

}
