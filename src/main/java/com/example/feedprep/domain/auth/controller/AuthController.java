package com.example.feedprep.domain.auth.controller;
import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.jwt.JwtUtil;
import com.example.feedprep.domain.auth.dto.*;
import com.example.feedprep.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    // 일반 회원 가입
    @PostMapping("/signup")
    ResponseEntity<ApiResponseDto<SignupResponseDto>> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        SignupResponseDto responseDto = authService.signup(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(SuccessCode.SIGNUP_SUCCESS, responseDto));
    }

    // 일반 로그인
    @PostMapping("/login")
    ResponseEntity<ApiResponseDto<TokenResponseDto>> login(@RequestBody @Valid LoginRequestDto requestDto) {

        Set<String> allowedRoles = Set.of("STUDENT", "PENDING_TUTOR");
        TokenResponseDto responseDto = authService.login(requestDto, allowedRoles);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(SuccessCode.LOGIN_SUCCESS, responseDto));
    }

    // 로그아웃 (일반, 관리자 포함)
    @PostMapping("/logout")
    ResponseEntity<ApiResponseDto<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDto.success(SuccessCode.LOGOUT_SUCCESS));
    }

//    // 회원 탈퇴
//    @PostMapping("/withdraw")
//    ResponseEntity<ApiResponseDto<Void>> withdraw(@RequestHeader("Authorization") String authHeader) {
//
//    }


}
