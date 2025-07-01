package com.example.feedprep.domain.feedbackreview.dto;

import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.feedprep.domain.feedbackreview.entity.FeedbackReview;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackReviewDetailsDto {

	private Long id;

	private Long userId;

	private Long tutorId;

	private Integer rating;

	private  String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private String modifiedAt;

	public FeedbackReviewDetailsDto(FeedbackReview feedbackReview) {
		this.id = feedbackReview.getId();
		this.userId = feedbackReview.getUserId();
		this.tutorId =feedbackReview.getTutorId();
		this.rating = feedbackReview.getRating();
		this.content = feedbackReview.getContent();
	    this.modifiedAt = feedbackReview.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}
