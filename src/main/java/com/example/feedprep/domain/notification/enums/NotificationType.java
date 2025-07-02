package com.example.feedprep.domain.notification.enums;
import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;

@Slf4j
@Getter
public enum NotificationType {

	//피드백
	FeedbackRequest(101, "%s 님께 피드백을 요청했습니다.", "null"),
	FeedbackComplete(102, "%s 님이 피드백을 작성했습니다.", "null"),
	FeedbackReject(103, "%s 님의 피드백 신청이 수락되었습니다.", "null"),
	FeedbackAccept(104, "%s 님의 피드백 신청이 거절되었습니다.", "null"),
	FeedbackReview(105, "%s 님의 피드백 리뷰가 등록되었습니다.", "null"),

	// 승인 관련
	ApprovalRequest(201, "%s 님이 튜터 승인을 요청했습니다.", "null"),
	ApprovalResult(202, "%s 님이 튜터로 승인되었습니다.", "null"),

	// 소셜 인터랙션
	CommentAdd(301, "%s 님이 댓글을 작성하셨습니다.", "null"),
	Follow(302, "%s 님이 구독했습니다.", "null"),
	Like(303, "%s 님이 게시글에 좋아요를 했습니다.", "null"),

	// 보상/이벤트
	Coupon(401, "%s 님께 쿠폰이 지급되었습니다.", "null");

	private final int code;
	private final String messageTemplate;
	private final String urlTemplate;

	NotificationType(int code, String messageTemplate, String urlTemplate) {
		this.code = code;
		this.messageTemplate = messageTemplate;
		this.urlTemplate = urlTemplate;
	}
	public static NotificationType fromNumber(int number){
		return Arrays.stream(NotificationType.values())
			.filter(type -> type.code == number)
			.findFirst()
			.orElseThrow(()->new CustomException(ErrorCode.BAD_REQUEST_STATE));
	}
	public Optional<String> buildMessage(Object... args) {
		try {
			return Optional.of(String.format(messageTemplate, args));
		} catch (Exception e) {

			return Optional.empty(); // 포맷 실패 시 안전하게 처리
		}
	}

	public Optional<String> buildUrl(Object... args) {
		if (urlTemplate == null) return Optional.empty();
		try {
			return Optional.of(String.format(urlTemplate, args));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
