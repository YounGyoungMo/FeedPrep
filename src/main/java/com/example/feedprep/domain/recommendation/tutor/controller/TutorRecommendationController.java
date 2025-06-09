package com.example.feedprep.domain.recommendation.tutor.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.domain.recommendation.tutor.dto.RecommendTutorDto;
import com.example.feedprep.domain.recommendation.tutor.dto.TutorResponseDto;
import com.example.feedprep.domain.recommendation.tutor.service.TutorRecommendationService;

import lombok.RequiredArgsConstructor;

@RestController("/tutor")
@RequiredArgsConstructor
public class TutorRecommendationController {

	private final TutorRecommendationService tutorRecommendationService;

	@GetMapping
	public ResponseEntity<ApiResponseDto<RecommendTutorDto>> recommendTutors(
		@AuthUser Long studentId,
		@RequestParam(defaultValue = "1") int page
	) {

		RecommendTutorDto recommendTutorDto = tutorRecommendationService.recommendTutors(studentId, page);

		return ResponseEntity.status(SuccessCode.RECOMMEND_TUTOR_LIST.getHttpStatus())
			.body(ApiResponseDto.success(SuccessCode.RECOMMEND_TUTOR_LIST, recommendTutorDto));
	}

}
