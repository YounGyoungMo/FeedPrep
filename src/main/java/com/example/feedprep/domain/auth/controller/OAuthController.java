package com.example.feedprep.domain.auth.controller;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.jwt.JwtUtil;
import com.example.feedprep.domain.auth.dto.OAuthUserResponseDto;
import com.example.feedprep.domain.auth.dto.TokenResponseDto;
import com.example.feedprep.domain.auth.entity.RefreshToken;
import com.example.feedprep.domain.auth.oauth.client.OAuthClient;
import com.example.feedprep.domain.auth.oauth.client.OAuthClientFactory;
import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import com.example.feedprep.domain.auth.repository.RefreshTokenRepository;
import com.example.feedprep.domain.auth.service.OAuthService;
import com.example.feedprep.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;
    private final OAuthClientFactory oAuthClientFactory;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // 카카오, 네이버, 구글 로그인 화면으로 리다이렉트
    // 가입 전에 role도 같이 전송
    @GetMapping("/authorize/{providerName}")
    public void redirectToProvider(
            @PathVariable String providerName,
            @RequestParam(value = "role", required = false) String role,
            HttpServletResponse response
    ) {
        try {
            String redirectUri = oAuthService.getRedirectUri(providerName,role);
            response.sendRedirect(redirectUri);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_REDIRECT_FAIL);
        }
    }

    // 로그인 성공시 해당 요청으로 자동 리다이렉트 (OAuth 제공 사이트에서 Redirect URI 설정)
    // 카카오, 네이버, 구글 등의 인가 코드 받아서 인가 코드 통해서 access token 요청후 소셜 로그인
    // 소셜로 회원가입 안되어 있으면 가입후 로그인
    // OAuth2 표준 키워드 state를 통해 role값 가져옴
    @GetMapping("/{provider}/callback")
    public ResponseEntity<ApiResponseDto<TokenResponseDto>> handleOAuthCallback(
            @PathVariable("provider") String providerName,
            @RequestParam("code") String code,
            @RequestParam("state") String roleState
    ) {
        String role = roleState.toUpperCase();
        OAuthProvider oAuthProvider = OAuthProvider.fromString(providerName);
        OAuthClient client = oAuthClientFactory.getClient(oAuthProvider);

        // 인가 코드로 provider 액세스 토큰 요청
        String providerAccessToken = client.requestAccessToken(providerName, code);

        // 액세스 토큰으로 사용자 정보 조회
        OAuthUserResponseDto userInfo = client.getUserInfo(providerAccessToken);

        // 소셜 회원가입 처리 또는 기존 소셜 계정 회원 반환
        User user = oAuthService.socialSignup(userInfo, role);

        // 내 사이트 액세스 토큰 생성
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshTokenString = jwtUtil.generateRefreshToken(user.getEmail());

        // 5. 기존 리프레시 토큰 갱신 또는 생성
        RefreshToken existingRefreshToken = refreshTokenRepository.findByUser_UserId(user.getUserId());
        if (existingRefreshToken != null) {
            existingRefreshToken.updateToken(refreshTokenString);
        } else {
            existingRefreshToken = new RefreshToken(refreshTokenString, user);
        }
        refreshTokenRepository.save(existingRefreshToken);


        TokenResponseDto responseDto = new TokenResponseDto(accessToken, refreshTokenString);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDto.success(SuccessCode.SOCIAL_OAUTH_LOGIN_SUCCESS, responseDto));
    }
}
