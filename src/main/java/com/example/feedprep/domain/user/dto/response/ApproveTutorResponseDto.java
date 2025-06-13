package com.example.feedprep.domain.user.dto.response;


import com.example.feedprep.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApproveTutorResponseDto {

    private UserRole role;
}
