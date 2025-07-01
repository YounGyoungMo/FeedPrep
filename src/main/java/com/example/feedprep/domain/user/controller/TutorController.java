package com.example.feedprep.domain.user.controller;


import static com.example.feedprep.common.exception.enums.SuccessCode.*;

import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.domain.user.service.TutorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/tutor")
@RequiredArgsConstructor
public class TutorController {

    private final TutorService tutorService;

    // 활동 승인 요청 보내기
    @PostMapping()
    public ResponseEntity<ApiResponseDto<Void>> sendRequestActivity(
        @AuthUser Long userId,
        @RequestParam Long adminId,
        @RequestParam Long documentId
    ) {
        tutorService.sendRequestActivity(userId, adminId, documentId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponseDto.success(SEND_REQUEST_ACTIVITY_SUCCESS));
    }

    // 보낸 요청 확인
    @GetMapping()
    public ResponseEntity<ApiResponseDto<List<TutorMessageResponseDdo>>> getRequestList(
        @AuthUser Long userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponseDto.success(GET_REQUEST_LIST_SUCCESS, tutorService.getRequestList(userId)));
    }

}
