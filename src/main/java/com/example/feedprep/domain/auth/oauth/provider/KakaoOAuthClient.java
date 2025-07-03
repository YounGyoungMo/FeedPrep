package com.example.feedprep.domain.auth.oauth.provider;

import com.example.feedprep.common.config.OAuthProperties;
import com.example.feedprep.common.config.ProviderConfig;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.auth.dto.OAuthUserResponseDto;
import com.example.feedprep.domain.auth.oauth.client.OAuthClient;
import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient implements OAuthClient {
    private final OAuthProperties oAuthProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    // 카카오 로그인시 사용자에게 보여줄 인증 요청 URL 생성
    // ex : https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}
    public String getAuthorizationUrl(String role) {
        ProviderConfig prop = oAuthProperties.getProviders().get(OAuthProvider.KAKAO);
        log.info(prop.getRedirectUri());
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(prop.getAuthorizeUri());
        return factory.builder()
                .queryParam("response_type", "code")
                .queryParam("client_id", prop.getClientId())
                .queryParam("redirect_uri", prop.getRedirectUri())
                .queryParam("state", role)
                .build()
                .toString();
    }

    public String requestAccessToken(String providerName, String code) {
        ProviderConfig prop = oAuthProperties.getProviders().get(OAuthProvider.KAKAO);
        String tokenUrl = prop.getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", prop.getRedirectUri());
        params.add("client_id", prop.getClientId());
        params.add("client_secret", prop.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        } else {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_TOKEN_REQUEST_FAIL);
        }
    }

    @Override
    // 카카오에서 생성된 액세스 토큰으로 카카오 사용자 정보 요청하는 메서드
    public OAuthUserResponseDto getUserInfo(String accessToken) {
        ProviderConfig config = oAuthProperties.getProviders().get(OAuthProvider.KAKAO);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                config.getUserInfoUri(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        // 응답 body에서 필요한 정보 추출
        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_USERINFO_FAIL);
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        // 추출된 정보 토대로 OAuthUserDto 생성
        return OAuthUserResponseDto.builder()
                .providerId(String.valueOf(body.get("id")))
                .email((String) kakaoAccount.get("email"))
                .nickname((String) profile.get("nickname"))
                .profileImageUrl((String) profile.get("profile_image_url"))
                .provider(OAuthProvider.KAKAO)
                .build();
    }




}
