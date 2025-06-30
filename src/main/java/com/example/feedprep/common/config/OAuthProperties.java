package com.example.feedprep.common.config;

import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "oauth")
@Component
public class OAuthProperties {
    private Map<OAuthProvider, ProviderConfig> providers;
}

