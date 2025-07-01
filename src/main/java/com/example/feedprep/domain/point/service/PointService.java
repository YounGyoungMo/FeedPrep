package com.example.feedprep.domain.point.service;

import java.util.List;

import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.point.entity.Point;
import com.example.feedprep.domain.user.entity.User;

public interface PointService {
	// 포인트 환불 - 피드백 거절에서 사용
	void refundPoint(FeedbackRequestEntity feedback);

	// 포인트 차감 - 피드백에서 사용 필요
	void makePayment(FeedbackRequestEntity feedback);

	// 포인트 추가 - 튜터 피드백 요청 수락 후
	void makeIncome(FeedbackRequestEntity feedback);

	void pointCharge(Long userId, String paymentId, Integer amount);

	Boolean checkCharge(String paymentId);

	void handleWebhook(String webhookSecret, String rawBody, String signature, String timestamp);

	// 포인트 확인
	boolean hasEnoughPoint(Long userId);

	List<Point> getPoint(Long userId);
}
