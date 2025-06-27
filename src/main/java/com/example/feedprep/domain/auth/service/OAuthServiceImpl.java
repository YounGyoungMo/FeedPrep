package com.example.feedprep.domain.auth.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.auth.dto.OAuthUserResponseDto;
import com.example.feedprep.domain.auth.oauth.client.OAuthClient;
import com.example.feedprep.domain.auth.oauth.client.OAuthClientFactory;
import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthServiceImpl implements OAuthService {
    private final OAuthClientFactory oAuthClientFactory;
    private final UserRepository userRepository;

    public String getRedirectUri(String providerName, String role) {
        OAuthProvider provider = switch (providerName.toLowerCase()) {
            case "kakao" -> OAuthProvider.KAKAO;
            case "google" -> OAuthProvider.GOOGLE;
            case "naver" -> OAuthProvider.NAVER;
            default -> throw new CustomException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        };

        OAuthClient client = oAuthClientFactory.getClient(provider);
        if (client == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }

        return client.getAuthorizationUrl(role);
    }

    public User socialSignup(OAuthUserResponseDto userInfo, String roleString) {
        // 소셜로 가입된 계정 있는지 확인
        User socialUser = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId());

        // 소셜 계정 이메일과 같은 이메일로 가입된 계정 있는지 확인
        Optional<User> userByEmail = userRepository.findByEmail(userInfo.getEmail());
        if (userByEmail.isPresent()) {
            User existingUser = userByEmail.get();
            // 일반 이메일 (소셜 연동X)일 경우 이메일 중복 처리
            if (existingUser.getProvider() == null) {
                throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }

        // 소셜 계정 없으면 새로 가입
        if (socialUser == null) {
            // 입력된 role을 대문자로 변환후 분기 처리
            String roleUpper = roleString.toUpperCase();
            if (roleUpper.equals("TUTOR")) {
                roleUpper = "PENDING_TUTOR";
            } else if (!roleUpper.equals("STUDENT")) {
                throw new CustomException(ErrorCode.INVALID_ROLE_REQUEST);
            }
            UserRole role = UserRole.valueOf(roleUpper);

            // 소셜 계정 가입후 저장
            socialUser = new User(userInfo.getNickname(), userInfo.getEmail(), userInfo.getProfileImageUrl(), role, userInfo.getProvider(), userInfo.getProviderId());
            userRepository.save(socialUser);
        }

        return socialUser;
    }


}
