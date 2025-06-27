package com.example.feedprep.domain.auth.service;

import com.example.feedprep.domain.auth.dto.OAuthUserResponseDto;
import com.example.feedprep.domain.user.entity.User;

public interface OAuthService {
    String getRedirectUri(String providerName, String role);
    User socialSignup(OAuthUserResponseDto userInfo, String role);
}
