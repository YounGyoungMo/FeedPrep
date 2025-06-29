package com.example.feedprep.domain.feedbackreview.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewListDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewRequestDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewDetailsDto;
import com.example.feedprep.domain.feedbackreview.service.FeedbackReviewService;

@RestController
@RequiredArgsConstructor
public class FeedbackReviewController {
	private final FeedbackReviewService feedbackReviewService;

	@PostMapping("/feedback/{feedbackId}/review")
	public ResponseEntity<ApiResponseDto<FeedbackReviewDetailsDto>> createReview(
		@AuthUser Long userId,
		@PathVariable Long feedbackId,
		@Validated @RequestBody FeedbackReviewRequestDto dto){
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponseDto.success(
				SuccessCode.OK_SUCCESS_FEEDBACK_REVIEW_CREATED,
				feedbackReviewService.createReview(userId, feedbackId, dto)));
	}
	@GetMapping("/feedback/review/{reviewId}")
	public ResponseEntity<ApiResponseDto<FeedbackReviewDetailsDto>> getReview(
		@AuthUser Long userId,
		@PathVariable Long reviewId){
		return  ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseDto.success(
				SuccessCode.OK_SUCCESS_FEEDBACK_REVIEW,
				feedbackReviewService.getReview(userId,reviewId)));
	}
	@GetMapping("/feedback/review")
	public ResponseEntity<ApiResponseDto<List<FeedbackReviewListDto>>> getReviews(
		@AuthUser Long userId,
		@RequestParam(defaultValue= "0") Integer page,
		@Validated @RequestParam(defaultValue = "20") Integer size){
		return  ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseDto.success(
				SuccessCode.OK_SUCCESS_FEEDBACK_REVIEW,
				feedbackReviewService.getReviews(userId, page, size)));
	}

	@PutMapping("/feedback/{feedbackId}/review/{reviewId}")
	public ResponseEntity<ApiResponseDto<FeedbackReviewDetailsDto>> updateReview(
		@AuthUser Long userId,
		@PathVariable Long reviewId,
		@Validated @RequestBody FeedbackReviewRequestDto dto){
		return  ResponseEntity.status(HttpStatus.OK)
			    .body(ApiResponseDto.success(
				SuccessCode.OK_SUCCESS_FEEDBACK_REVIEW_UPDATE,
				feedbackReviewService.updateReview(userId, reviewId, dto)));
	}
	@DeleteMapping("/feedback/{feedbackId}/review/{reviewId}")
	public ResponseEntity<ApiResponseDto> deleteReview(
		@AuthUser Long userId,
		@PathVariable Long reviewId
	){
	return new ResponseEntity<>(feedbackReviewService.deleteReview(userId, reviewId), HttpStatus.OK);
	}
}
