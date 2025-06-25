package com.example.feedprep.domain.auth.oauth.client;
import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component

public class OAuthClientFactory {
    private final Map<OAuthProvider, OAuthClient> clientMap;

    public OAuthClientFactory(List<OAuthClient> clients) {
        clientMap = new HashMap<>();
        clients.forEach(client -> clientMap.put(client.getProvider(), client));
    }

    public OAuthClient getClient(OAuthProvider provider) {
        return clientMap.get(provider);
    }

}