package com.example.feedprep.common.config;

import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import lombok.Data;

import java.util.Map;

@Data
public class ProviderConfig {
    private OAuthProvider provider;
    private String clientId;
    private String authorizeUri;
    private String tokenUri;
    private String userInfoUri;
    private String redirectUri;
    private Map<String, String> fields;
}
