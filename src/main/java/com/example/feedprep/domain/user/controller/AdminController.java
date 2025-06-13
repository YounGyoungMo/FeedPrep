package com.example.feedprep.domain.user.controller;

import static com.example.feedprep.common.exception.enums.SuccessCode.*;

import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.techstack.dto.CreateTechStackRequestDto;
import com.example.feedprep.domain.techstack.service.TechStackService;
import com.example.feedprep.domain.user.dto.response.TutorResponseDto;
import com.example.feedprep.domain.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/authority")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/tutor/{tutorId}")
    public ResponseEntity<ApiResponseDto<TutorResponseDto>> approveTutor(
        @PathVariable Long tutorId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponseDto.success(APPROVE_TUTOR,adminService.approveTutor(tutorId)));
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
