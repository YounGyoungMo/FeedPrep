package com.example.feedprep.common.security.jwt;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorCode errorCode = ErrorCode.UNAUTHORIZED; // 기본값

        if (authException instanceof CustomException e) {
            errorCode = e.getErrorCode();
        }

        ApiResponseDto<?> apiResponse = ApiResponseDto.fail(errorCode, request.getRequestURI());

        String json = new ObjectMapper().writeValueAsString(apiResponse);
        response.getWriter().write(json);
    }
}
