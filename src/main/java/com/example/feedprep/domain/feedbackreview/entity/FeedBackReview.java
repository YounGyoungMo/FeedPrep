package com.example.feedprep.domain.feedbackreview.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Repository;
import com.example.feedprep.common.entity.BaseTimeEntity;
import com.example.feedprep.domain.feedback.entity.FeedBack;
import com.example.feedprep.domain.user.entity.User;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feedbackreviews")
public class FeedBackReview extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int Rating;
	private  String Content;

	@OneToOne
	@JoinColumn(name = "feedback_id")
	private FeedBack feedback;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;


}
