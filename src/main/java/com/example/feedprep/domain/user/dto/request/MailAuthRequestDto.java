package com.example.feedprep.domain.user.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailAuthRequestDto {

    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @Pattern(regexp = "\\d{6}", message = "6자리 숫자여야 합니다.")
    private Long authNumber;

    @NotBlank(message = "새로운 비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
        message = "비밀번호는 소영문,대영문자와 숫자를 포함한 8자 이상이어야 합니다.")
    private String newPassword;
}
