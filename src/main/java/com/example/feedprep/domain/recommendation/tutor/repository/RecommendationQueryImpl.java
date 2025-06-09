package com.example.feedprep.domain.recommendation.tutor.repository;

import static com.querydsl.core.types.dsl.Expressions.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.feedprep.domain.feedback.entity.QFeedBack;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.entity.QFeedbackRequestEntity;
import com.example.feedprep.domain.feedbackreview.entity.QFeedBackReview;
import com.example.feedprep.domain.techstack.entity.QUserTechStack;
import com.example.feedprep.domain.user.entity.QUser;
import com.example.feedprep.domain.user.enums.UserRole;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecommendationQueryImpl implements RecommendationQuery{

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Tuple> recommendTutors(Long userId, int page) {
		QFeedbackRequestEntity feedbackRequest = QFeedbackRequestEntity.feedbackRequestEntity;
		QUser user = QUser.user;
		QFeedBack feedBack = QFeedBack.feedBack;
		QFeedBackReview review = QFeedBackReview.feedBackReview;
		QUserTechStack userTechStack = QUserTechStack.userTechStack;

		// 잔여 피드백 요청
		NumberExpression<Long> pendingOrApprovedCount = new CaseBuilder()
			.when(feedbackRequest.requestState.eq(RequestState.PENDING)
				.or(feedbackRequest.requestState.eq(RequestState.APPROVED)))
			.then(1L)
			.otherwise(0L)
			.sum();

		return jpaQueryFactory
			.select(
				user.userId.as("tutorId"),
				user.name.as("name"),
				review.Rating.avg().as("rating"),
				user.introduction.as("introduction"),
				pendingOrApprovedCount
			)
			.from(user)
			.leftJoin(feedbackRequest).on(user.eq(feedbackRequest.tutor))
			.leftJoin(feedBack).on(feedbackRequest.eq(feedBack.feedBackRequest))
			.leftJoin(review).on(feedBack.eq(review.feedback))
			.where(
				user.userId.in(
						JPAExpressions
							.select(userTechStack.user.userId)
							.from(userTechStack)
							.where(
								userTechStack.techStack.techId.in(
									JPAExpressions
										.select(userTechStack.techStack.techId)
										.from(userTechStack)
										.where(userTechStack.user.userId.eq(userId))
								)
							)
					)
					.and(user.role.eq(UserRole.APPROVED_TUTOR))
			)
			.groupBy(user.userId)
			.orderBy(
				pendingOrApprovedCount.asc(),
				review.Rating.avg().desc()
			)
			.offset(page)
			.limit(4)
			.fetch();
	}
}
