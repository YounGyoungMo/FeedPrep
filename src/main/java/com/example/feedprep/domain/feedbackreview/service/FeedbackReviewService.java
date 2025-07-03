package com.example.feedprep.domain.feedbackreview.service;

import java.util.List;
import java.util.Map;

import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewListDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewRequestDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewDetailsDto;

public interface FeedbackReviewService {

    //리뷰 추가
	FeedbackReviewDetailsDto createReview( Long userId, Long feedbackId, FeedbackReviewRequestDto dto);
	//리뷰 단건 조회
	FeedbackReviewDetailsDto getReview(Long userId, Long reviewId);

	List<FeedbackReviewListDto> getReviews(Long userId, Integer page, Integer size);

	//튜터 평점 조회
	Double getAverageRating(Long tutorId);
	//리뷰 수정
	FeedbackReviewDetailsDto updateReview( Long userId, Long reviewId, FeedbackReviewRequestDto dto);
	//리뷰의 삭제
	Map<String, Object> deleteReview(Long userId, Long reviewId);
}
