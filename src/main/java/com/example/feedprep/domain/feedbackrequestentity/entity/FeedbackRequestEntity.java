package com.example.feedprep.domain.feedbackrequestentity.entity;

import jakarta.persistence.Column;
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

import com.example.feedprep.common.entity.BaseTimeEntity;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.feedback.entity.FeedBack;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
import com.example.feedprep.domain.user.entity.User;

@Getter
@Entity
@Table(name = "feedbackrequestentitys")
public class FeedbackRequestEntity extends BaseTimeEntity {
	//속성
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "tutor_id", nullable = false)
	private User tutor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id",nullable = false)
	private Document document;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feedback_id")
	private FeedBack feedBack;

	private String content;

	@Column(nullable = false)
	private RequestState requestState;

	//생성자
	public FeedbackRequestEntity(){}

	public FeedbackRequestEntity(FeedbackRequestDto dto, User user, User tutor, Document document) {
		this.user = user;
		this.tutor = tutor;
		this.document = document;
		this.content = dto.getContent();
	}

	public FeedbackRequestEntity(User user, Document document, RequestState requestState) {
		this.user = user;
		this.document = document;
		this.requestState = requestState;
	}

	//변경
	public void updateRequestState(RequestState requestState){
		this.requestState = requestState;
	}
	public void updateFeedbackRequestEntity(FeedbackRequestDto dto, User tutor, Document document){
		this.tutor = tutor;
		this.document = document;
		this.content = dto.getContent();
	}

}
