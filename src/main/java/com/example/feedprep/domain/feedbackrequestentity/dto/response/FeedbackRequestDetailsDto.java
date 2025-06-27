package com.example.feedprep.domain.feedbackrequestentity.dto.response;

import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.example.feedprep.domain.feedbackrequestentity.common.RejectReason;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@RequiredArgsConstructor
public class FeedbackRequestDetailsDto {
	private Long id;
	private Long tutorId;
	private Long userId;
	private Long documentId;
	private String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private RequestState requestState;
	private String state;
	private RejectReason rejectReason;
	private String etcContent;
	private String modifiedAt;

	public FeedbackRequestDetailsDto(FeedbackRequestEntity entity){
		this.id = entity.getId();
		this.tutorId =entity.getTutor().getUserId();
		this.userId = entity.getUser().getUserId();
		this.documentId = entity.getDocument().getDocumentId();
		this.content = entity.getContent();
		this.requestState = entity.getRequestState();
		this.state = requestState.getDescription();
		this.rejectReason = entity.getRejectReason();
		this.etcContent = entity.getEtcContent();
		this.modifiedAt = entity.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}
