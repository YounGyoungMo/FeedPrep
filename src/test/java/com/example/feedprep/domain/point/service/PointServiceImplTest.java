package com.example.feedprep.domain.point.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.point.dto.PaymentResponseDto;
import com.example.feedprep.domain.point.entity.Point;
import com.example.feedprep.domain.point.enums.PointType;
import com.example.feedprep.domain.point.repository.PointRepository;
import com.example.feedprep.domain.point.util.WebhookVerifier;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {

	public static final Integer FEEDBACK_COST = 5000;
	@Mock
	private PointRepository pointRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private PaymentService paymentService;

	@InjectMocks
	private PointServiceImpl pointService;


	@Test
	void refundPoint_성공() {
		Point pointStudent = mock(Point.class);
		FeedbackRequestEntity feedback = mock(FeedbackRequestEntity.class);
		User tutor = mock(User.class);

		when(pointStudent.getType()).thenReturn(PointType.USE);
		when(feedback.getUser()).thenReturn(tutor);


		List<Point> pointList = List.of(pointStudent);
		when(pointRepository.findByFeedback(feedback)).thenReturn(pointList);
		pointService.refundPoint(feedback);

		ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
		verify(pointRepository).save(captor.capture());

		Point savedPoint = captor.getValue();
		assertEquals(PointType.REFUND, savedPoint.getType());
		assertEquals(tutor, savedPoint.getUser());
		assertEquals(feedback, savedPoint.getFeedback());
		assertEquals(FEEDBACK_COST, savedPoint.getAmount());
	}
	@Test
	void refundPoint_실패_조회된_사용_내역이_없음() {
		// given
		FeedbackRequestEntity feedback = mock(FeedbackRequestEntity.class);
		when(pointRepository.findByFeedback(feedback)).thenReturn(Collections.emptyList());

		// when & then
		assertThrows(CustomException.class, () -> pointService.refundPoint(feedback));
	}

	@Test
	void makePayment_성공() {
		FeedbackRequestEntity feedback = mock(FeedbackRequestEntity.class);
		User user = mock(User.class);

		when(feedback.getUser()).thenReturn(user);

		pointService.makePayment(feedback);

		ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
		verify(pointRepository).save(captor.capture());

		Point saved = captor.getValue();
		assertEquals(-FEEDBACK_COST, saved.getAmount());
		assertEquals(PointType.USE, saved.getType());
		assertEquals(feedback, saved.getFeedback());
		assertEquals(user, saved.getUser());

	}

	@Test
	void makeIncome_성공() {
		FeedbackRequestEntity feedback = mock(FeedbackRequestEntity.class);
		User tutor = mock(User.class);

		when(feedback.getTutor()).thenReturn(tutor);
		pointService.makeIncome(feedback);

		ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
		verify(pointRepository).save(captor.capture());

		Point saved = captor.getValue();
		assertEquals(FEEDBACK_COST, saved.getAmount());
		assertEquals(PointType.INCOME, saved.getType());
		assertEquals(feedback, saved.getFeedback());
		assertEquals(tutor, saved.getUser());
	}

	@Test
	void handleWebhook_실패_서명_문제() {
		// given
		String rawBody = "{ ... }";
		String signature = "sig";
		String timestamp = "ts";
		String webhookSecret = "secret";

		// WebhookVerifier가 false 반환하도록 설정
		MockedStatic<WebhookVerifier> verifierMock = mockStatic(WebhookVerifier.class);
		verifierMock.when(() ->
			WebhookVerifier.verify(webhookSecret, rawBody, signature, timestamp)
		).thenReturn(false);


		// when & then
		assertThrows(CustomException.class, () ->
			pointService.handleWebhook(webhookSecret, rawBody, signature, timestamp)
		);
		verifierMock.close();

	}

	@Test
	void handleWebhook_성공() throws Exception {
		// given
		String rawBody = """
        {
            "type": "Transaction.Paid",
            "data": {
                "paymentId": "pid_123"
            }
        }
        """;
		String webhookSecret = "secret";
		String signature = "sig";
		String timestamp = "ts";

		try (MockedStatic<WebhookVerifier> verifierMock = mockStatic(WebhookVerifier.class)) {
			verifierMock.when(() ->
				WebhookVerifier.verify(webhookSecret, rawBody, signature, timestamp)
			).thenReturn(true);

			ObjectMapper realMapper = new ObjectMapper();
			JsonNode root = realMapper.readTree(rawBody);
			when(objectMapper.readTree(rawBody)).thenReturn(root);

			Point pending = new Point(5000, PointType.PENDING, null, null);
			pending.setPaymentId("pid_123");
			when(pointRepository.findByPaymentIdOrElseThrow("pid_123", false)).thenReturn(pending);

			PaymentResponseDto payment = mock(PaymentResponseDto.class);
			PaymentResponseDto.Amount amount = mock(PaymentResponseDto.Amount.class);
			when(amount.getTotal()).thenReturn(5000);
			when(payment.getAmount()).thenReturn(amount);
			when(paymentService.getPayment("pid_123")).thenReturn(payment);

			pointService.handleWebhook(webhookSecret, rawBody, signature, timestamp);

			assertEquals(PointType.CHARGE, pending.getType());
			verify(pointRepository).saveAndFlush(pending);
		}
	}

	@Test
	void pointCharge_성공() {
		Long userId = 1L;
		String paymentId = "pid_123";
		int amount = 5000;

		User mockUser = new User();
		mockUser.setUserId(userId);

		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(mockUser);

		pointService.pointCharge(userId, paymentId, amount);

		ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
		verify(pointRepository).save(captor.capture());

		Point savedPoint = captor.getValue();
		assertEquals(paymentId, savedPoint.getPaymentId());
		assertEquals(amount, savedPoint.getAmount());
		assertEquals(mockUser, savedPoint.getUser());
	}

	@Test
	void pointCharge_실패_유저가_다름() {
		Long pathUserId = 1L;
		Long entityUserId = 2L;
		String paymentId = "pid_123";
		int amount = 5000;

		User mockUser = new User();
		mockUser.setUserId(entityUserId);

		when(userRepository.findByIdOrElseThrow(pathUserId)).thenReturn(mockUser);

		assertThrows(CustomException.class, () ->
			pointService.pointCharge(pathUserId, paymentId, amount)
		);
	}
}
