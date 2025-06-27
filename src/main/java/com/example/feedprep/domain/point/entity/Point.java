package com.example.feedprep.domain.point.entity;

import java.util.List;

import com.example.feedprep.common.entity.BaseTimeEntity;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.point.enums.PointType;
import com.example.feedprep.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "point")
@NoArgsConstructor
public class Point extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pointId;

	@Column(nullable = false)
	private Integer amount;

	@Column(nullable = false)
	private boolean deleted = false;

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PointType type;

	// 충전의 경우에만
	@Column(unique = true)
	private String paymentId;

	@ManyToOne
	private FeedbackRequestEntity feedback;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User user;

	public Point(Integer amount, PointType type, FeedbackRequestEntity feedback, User user) {
		this.amount = amount;
		this.type = type;
		this.feedback = feedback;
		this.user = user;
	}

	// 충전
	public Point(Integer amount, String paymentId, User user) {
		this.amount = amount;
		this.type = PointType.PENDING;
		this.paymentId = paymentId;
		this.user = user;
	}

}
