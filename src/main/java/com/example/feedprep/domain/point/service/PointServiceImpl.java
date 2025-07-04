package com.example.feedprep.domain.point.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.point.dto.PaymentResponseDto;
import com.example.feedprep.domain.point.dto.PaymentSummaryDto;
import com.example.feedprep.domain.point.entity.Point;
import com.example.feedprep.domain.point.enums.PointType;
import com.example.feedprep.domain.point.repository.PointRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointServiceImpl implements PointService{

	public static final Integer FEEDBACK_COST = 5000;

	private final PointRepository pointRepository;

	private final UserRepository userRepository;

	private final ObjectMapper objectMapper;

	private final PaymentService paymentService;

	// 포인트 환불 - 피드백 거절 및 피드백 취소에서 사용
	@Transactional
	@Override
	public void refundPoint(FeedbackRequestEntity feedback) {
		List<Point> PointList = pointRepository.findByFeedback(feedback);
		if(PointList.isEmpty()){
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		for(Point p : PointList){
			if(p.getType().equals(PointType.USE)){
				Point refundStudent = new Point(FEEDBACK_COST, PointType.REFUND, feedback, feedback.getUser());
				pointRepository.save(refundStudent);
			}
		}
	}

	// 포인트 차감 - 피드백에서 사용 필요
	@Override
	public void makePayment(FeedbackRequestEntity feedback) {
		Point pointStudent = new Point(-FEEDBACK_COST, PointType.USE, feedback, feedback.getUser());
		pointRepository.save(pointStudent);
	}

	// 수락 후 사용
	@Override
	public void makeIncome(FeedbackRequestEntity feedback) {
		Point pointTutor = new Point(FEEDBACK_COST, PointType.INCOME, feedback, feedback.getTutor());
		pointRepository.save(pointTutor);
	}

	@Override
	public void pointCharge(Long userId, String paymentId, Integer amount) {
		User user = userRepository.findByIdOrElseThrow(userId);
		if(!user.getUserId().equals(userId)){
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		Point point = new Point(amount, paymentId, user);

		pointRepository.save(point);
	}

	@Override
	public Boolean checkCharge(String paymentId) {
		Point charge = pointRepository.findByPaymentIdOrElseThrow(paymentId, false);

		return charge.getType().equals(PointType.CHARGE);
	}

	@Override
	@Transactional
	public void handleWebhook(String webhookSecret, String rawBody, String signature, String timestamp) {
		try {
			// // 웹훅 검증
			// if (!com.example.feedprep.domain.point.util.WebhookVerifier.verify(webhookSecret, rawBody, signature, timestamp)) {
			// 	log.error("검증 실패");
			// 	throw new CustomException(ErrorCode.BAD_REQUEST);
			// }

			// JSON 파싱
			JsonNode root = objectMapper.readTree(rawBody);
			System.out.println(rawBody);
			JsonNode data = root.path("data");
			if (!data.has("paymentId")) {
				System.out.println("파싱 실패");
				System.out.println("파싱 실패");
				throw new CustomException(ErrorCode.BAD_REQUEST);
			}

			String type = root.path("type").asText();;
			String paymentId = data.get("paymentId").asText();

			if(!type.equals("Transaction.Paid")){
				System.out.println("결제 상태 불량");
				throw new CustomException(ErrorCode.BAD_REQUEST);
			}

			// 결제 정보 조회
			PaymentResponseDto payment = paymentService.getPayment(paymentId);
			if (payment == null) {
				System.out.println("데이터 이상");
				throw new CustomException(ErrorCode.BAD_REQUEST);
			}

			// 충전 완료
			Point pendingHistory = pointRepository.findByPaymentIdOrElseThrow(paymentId, false);
			if (payment.getAmount().getTotal().equals(pendingHistory.getAmount())) {
				pendingHistory.setType(PointType.CHARGE);
				pointRepository.saveAndFlush(pendingHistory);
				System.out.println("충전성공");
			} else {
				// 금액 불일치 시 처리 로직
				System.out.println("금액 불일치");
				throw new CustomException(ErrorCode.BAD_REQUEST);
			}
		} catch (Exception e) {
			System.out.println("예외처리");
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
	}

	@Override
	public boolean hasEnoughPoint(Long userId){
		Integer totalPoint = pointRepository.findTotalPointByUserId(userId);
		return totalPoint >= FEEDBACK_COST;
	}

	@Override
	public List<Point> getPointHistory(Long userId) {
		User user = userRepository.findByIdOrElseThrow(userId);
		return pointRepository.findAllByUser(user);
	}

	@Override
	public Integer getPoint(Long userId) {
		return pointRepository.findTotalPointByUserId(userId);
	}

	@Transactional
	@Scheduled(cron = "0 0 4 * * *")
	@Override
	public void verifyPaymentRecords() {
		List<PaymentSummaryDto> oneDayPayment = paymentService.getAllPayment();
		for(PaymentSummaryDto paymentSummaryDto : oneDayPayment){
			List<Point> point = pointRepository.findByPaymentIdAndType(
				paymentSummaryDto.getPaymentId(),
				PointType.CHARGE
			);
			if(!point.get(0).getAmount().equals(paymentSummaryDto.getTotalAmount())){
				log.info("결제 금액 불일치 - {}", paymentSummaryDto.getPaymentId());
				log.info("PortOne : {}", paymentSummaryDto.getTotalAmount());
				log.info("DB : {}", point.get(0).getAmount());
				point.get(0).setAmount(paymentSummaryDto.getTotalAmount());
			}
		}
	}
}
