package com.example.feedprep.domain.auth.dto;

import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import com.example.feedprep.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserResponseDto {
    private String nickname;
    private String email;
    private String profileImageUrl;
    private UserRole role;

    private OAuthProvider provider;
    private String providerId;
}
