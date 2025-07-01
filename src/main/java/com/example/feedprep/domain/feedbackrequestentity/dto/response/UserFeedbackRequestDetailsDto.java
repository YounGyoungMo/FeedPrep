package com.example.feedprep.domain.feedbackrequestentity.dto.response;

import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@RequiredArgsConstructor
public class UserFeedbackRequestDetailsDto {
	private Long id;
	private Long tutorId;
	private Long documentId;
	private String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private RequestState requestState;
	private String state;
	private String modifiedAt;

	public UserFeedbackRequestDetailsDto(FeedbackRequestEntity entity){
		this.id = entity.getId();
		this.tutorId = entity.getTutor().getUserId();
		this.documentId = entity.getDocument().getDocumentId();
		this.content = entity.getContent();
		this.requestState = entity.getRequestState();
		this.state = requestState.getDescription();
		this.modifiedAt = entity.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}


}
