package com.example.feedprep.domain.user.controller;

import static com.example.feedprep.common.exception.enums.SuccessCode.*;

import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.domain.techstack.dto.CreateTechStackRequestDto;
import com.example.feedprep.domain.user.dto.response.ApproveTutorResponseDto;
import com.example.feedprep.domain.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/admin/authority")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 받은 메세지 확인
    @GetMapping("/tutor")
    public ResponseEntity<ApiResponseDto<TutorMessageResponseDdo>> getMessageTutor(
        @RequestParam Long messageId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponseDto.success(GET_TUTOR_MESSAGE,adminService.getMessageTutor(messageId)));
    }

    @PutMapping("/tutor/{tutorId}")
    public ResponseEntity<ApiResponseDto<ApproveTutorResponseDto>> approveTutor(
        @AuthUser Long adminId,
        @PathVariable Long tutorId,
        @RequestParam(required = false) Long messageId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponseDto.success(APPROVE_TUTOR,adminService.approveTutor(adminId, tutorId, messageId)));
    }

    @PostMapping("/tech-stacks")
    public ResponseEntity<ApiResponseDto<Void>> createTechStack(
        @RequestBody CreateTechStackRequestDto requestDto
    ) {

        adminService.createTechStack(requestDto);

        return ResponseEntity.status(SuccessCode.TECH_STACK_CREATED.getHttpStatus())
            .body(ApiResponseDto.success(SuccessCode.TECH_STACK_CREATED));
    }

    @DeleteMapping("/tech-stacks/{techId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteTechStack(
        @PathVariable Long techId
    ) {

        adminService.deleteTechStack(techId);

        return ResponseEntity.status(SuccessCode.TECH_STACK_DELETED.getHttpStatus())
            .body(ApiResponseDto.success(SuccessCode.TECH_STACK_DELETED));
    }
}
