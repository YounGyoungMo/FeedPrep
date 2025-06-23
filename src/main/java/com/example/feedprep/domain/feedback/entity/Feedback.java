package com.example.feedprep.domain.feedback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.example.feedprep.common.entity.BaseTimeEntity;
import com.example.feedprep.domain.feedback.dto.request.FeedbackWriteRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.user.entity.User;

@Getter
@Entity
@Table(name = "feedbacks")
@RequiredArgsConstructor
public class Feedback extends BaseTimeEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name="request_id")
	private FeedbackRequestEntity feedbackRequestEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tutor_id", nullable = false)
	private User tutor;

	private String content;

	public Feedback(FeedbackWriteRequestDto requestDto, User tutor){
		this.content = requestDto.getContent();
		this.tutor =tutor;
	}

	public void updateFeedback(FeedbackWriteRequestDto requestDto){
		this.content = requestDto.getContent();
	}

	public void updateFeedbackRequest(FeedbackRequestEntity entity){
		this.feedbackRequestEntity = entity;
	}
}
