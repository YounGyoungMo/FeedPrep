package com.example.feedprep.domain.matching.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.domain.matching.dto.MatchingDto;
import com.example.feedprep.domain.matching.service.TutorMatchingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/tutor")
@RequiredArgsConstructor
public class TutorMatchingController {

	private final TutorMatchingService tutorMatchingService;

	@GetMapping
	public ResponseEntity<ApiResponseDto<List<MatchingDto>>> tutorMatching(
		@AuthUser Long studentId,
		@RequestParam(defaultValue = "1") int page
	) {

		List<MatchingDto> tutorList = tutorMatchingService.tutorMatching(studentId, page);

		return ResponseEntity.status(SuccessCode.GET_TUTORLIST_SUCCESS.getHttpStatus())
			.body(ApiResponseDto.success(SuccessCode.GET_TUTORLIST_SUCCESS, tutorList));
	}
}
