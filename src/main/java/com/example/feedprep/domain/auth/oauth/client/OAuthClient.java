package com.example.feedprep.domain.auth.oauth.client;

import com.example.feedprep.domain.auth.dto.OAuthUserResponseDto;
import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;

public interface OAuthClient {
    OAuthProvider getProvider();
    String getAuthorizationUrl(String role);
    OAuthUserResponseDto getUserInfo(String accessToken);
    String requestAccessToken(String provierName, String code);
}
