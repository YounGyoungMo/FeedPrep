package com.example.feedprep.domain.feedbackrequestentity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedbackRequestDto {
	private Long tutorId;
	private Long documentId;
	private String content;
}
