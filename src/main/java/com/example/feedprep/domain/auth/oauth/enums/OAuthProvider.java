package com.example.feedprep.domain.auth.oauth.enums;

public enum OAuthProvider {
    KAKAO, GOOGLE, NAVER;

    public static OAuthProvider fromString(String provider) {
        return OAuthProvider.valueOf(provider.toUpperCase());
    }
}
