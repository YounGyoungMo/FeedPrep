package com.example.feedprep.domain.feedback.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.example.feedprep.common.entity.BaseTimeEntity;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;

@Getter
@Entity
@Table(name = "feedbackresponses")
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class FeedBack extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private FeedbackRequestEntity feedBackRequest;

	//private tutor


	private String content;

	public FeedBack(FeedbackRequestEntity feedBackRequest, String content) {
		this.feedBackRequest = feedBackRequest;
		this.content = content;
	}
}
