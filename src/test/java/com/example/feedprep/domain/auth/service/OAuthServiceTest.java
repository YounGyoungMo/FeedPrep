package com.example.feedprep.domain.auth.service;

import com.example.feedprep.domain.auth.dto.OAuthUserResponseDto;
import com.example.feedprep.domain.auth.oauth.client.OAuthClient;
import com.example.feedprep.domain.auth.oauth.client.OAuthClientFactory;
import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class OAuthServiceTest {
    private AutoCloseable closeable;

    @Mock
    private OAuthClientFactory oAuthClientFactory;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OAuthServiceImpl oAuthService;

    // 인가 코드 설정
    private final String kakaoCode = "auth_code_kakao";
    private final String googleCode = "auth_code_google";

    private final OAuthUserResponseDto kakaoUser = OAuthUserResponseDto.builder()
            .provider(OAuthProvider.KAKAO)
            .providerId("kakao-123")
            .email("kakao@example.com")
            .nickname("카카오유저")
            .profileImageUrl("http://kakao.com/profile.png")
            .build();

    private final OAuthUserResponseDto googleUser = OAuthUserResponseDto.builder()
            .provider(OAuthProvider.GOOGLE)
            .providerId("google-123")
            .email("google@example.com")
            .nickname("구글유저")
            .profileImageUrl("http://google.com/profile.png")
            .build();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void 성공_소셜_로그인_카카오() {
        // given
        when(oAuthClientFactory.getClient(OAuthProvider.KAKAO)).thenReturn(oAuthClient);
        when(oAuthClient.requestAccessToken("kakao", kakaoCode)).thenReturn("mockAccessToken");
        when(oAuthClient.getUserInfo("mockAccessToken")).thenReturn(kakaoUser);

        User user = User.builder()
                .email(kakaoUser.getEmail())
                .name(kakaoUser.getNickname())
                .role(UserRole.STUDENT)
                .build();
        when(userRepository.findByEmail(kakaoUser.getEmail())).thenReturn(java.util.Optional.of(user));

        // when
        User result = oAuthService.socialSignup(kakaoUser, "STUDENT");

        // then
        assertThat(result.getEmail()).isEqualTo("kakao@example.com");
        assertThat(result.getRole()).isEqualTo(UserRole.STUDENT);
    }

    @Test
    void 성공_소셜_로그인_구글() {
        // given
        when(oAuthClientFactory.getClient(OAuthProvider.GOOGLE)).thenReturn(oAuthClient);
        when(oAuthClient.requestAccessToken("google", googleCode)).thenReturn("mockAccessToken");
        when(oAuthClient.getUserInfo("mockAccessToken")).thenReturn(googleUser);

        User user = User.builder()
                .email(googleUser.getEmail())
                .name(googleUser.getNickname())
                .role(UserRole.STUDENT)
                .build();
        when(userRepository.findByEmail(googleUser.getEmail())).thenReturn(java.util.Optional.of(user));

        // when
        User result = oAuthService.socialSignup(googleUser, "STUDENT");

        // then
        assertThat(result.getEmail()).isEqualTo("google@example.com");
        assertThat(result.getRole()).isEqualTo(UserRole.STUDENT);
    }
}
