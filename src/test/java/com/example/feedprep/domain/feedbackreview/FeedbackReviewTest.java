package com.example.feedprep.domain.feedbackreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.feedback.entity.Feedback;
import com.example.feedprep.domain.feedback.repository.FeedBackRepository;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewListDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewRequestDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewDetailsDto;
import com.example.feedprep.domain.feedbackreview.entity.FeedbackReview;
import com.example.feedprep.domain.feedbackreview.repository.FeedBackReviewRepository;
import com.example.feedprep.domain.feedbackreview.service.FeedbackReviewServiceImpl;
import com.example.feedprep.domain.notification.service.NotificationService;
import com.example.feedprep.domain.notification.service.NotificationServiceImpl;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
 public class FeedbackReviewTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private FeedBackReviewRepository feedBackReviewRepository;

	@Mock
	private FeedBackRepository feedBackRepository;
	@Mock
	private NotificationServiceImpl notificationService;
	@InjectMocks
	private FeedbackReviewServiceImpl feedbackReviewService;

	@Test
	public void 리뷰_생성_테스트() {

		Long studentId = 1L;
		Long tutorId = 2L;
		User tutor = mock(User.class);
		User student = mock(User.class);
		when(userRepository.findByIdOrElseThrow(studentId)).thenReturn(student);
		when(student.getUserId()).thenReturn(studentId);
		when(tutor.getUserId()).thenReturn(2L); // 적당한 ID

		FeedbackRequestEntity feedbackRequestEntity = mock(FeedbackRequestEntity.class);
		when(feedbackRequestEntity.getUser()).thenReturn(student);

		Long feedbackId = 1L;
		Feedback feedback = mock(Feedback.class);
		when(feedback.getTutor()).thenReturn(tutor);
		when(feedback.getFeedbackRequestEntity()).thenReturn(feedbackRequestEntity);
		when(feedBackRepository.findWithRequestAndUserById(feedbackId)).thenReturn(Optional.of(feedback));

		FeedbackReview feedbackReview = mock(FeedbackReview.class);
		when(feedbackReview.getId()).thenReturn(1L);
		when(feedBackReviewRepository.save(any())).thenReturn(feedbackReview);

		FeedbackReviewRequestDto feedbackReviewRequestDto = mock(FeedbackReviewRequestDto.class);
		when(feedbackReviewRequestDto.getRating()).thenReturn(5);
		when(feedbackReviewRequestDto.getContent()).thenReturn("좋은 말씀 감사합니다");
		when(feedbackReview.getModifiedAt()).thenReturn(LocalDateTime.now());

		assertDoesNotThrow(() -> feedbackReviewService.createReview(studentId, feedbackId, feedbackReviewRequestDto));
		verify(notificationService, times(1))
			.sendNotification(studentId, tutorId, 102);
		verify(feedBackReviewRepository, times(1)).save(any());
	}
	@Test
	void 리뷰생성_실패_존재하지않는유저() {
		Long userId = 1L;
		Long feedbackId = 2L;
		FeedbackReviewRequestDto dto = mock(FeedbackReviewRequestDto.class);

		when(userRepository.findByIdOrElseThrow(userId))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.createReview(userId, feedbackId, dto)
		);

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 리뷰생성_실패_존재하지않는피드백() {
		Long userId = 1L;
		Long feedbackId = 2L;
		FeedbackReviewRequestDto dto = mock(FeedbackReviewRequestDto.class);

		User user = mock(User.class);
		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);

		when(feedBackRepository.findWithRequestAndUserById(feedbackId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_FEEDBACK));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.createReview(userId, feedbackId, dto)
		);

		assertEquals(ErrorCode.NOT_FOUND_FEEDBACK, exception.getErrorCode());
	}

	@Test
	void 리뷰생성_실패_작성자가아님() {
		Long userId = 1L;
		Long tutorId = 2L;
		Long feedbackId = 2L;
		FeedbackReviewRequestDto dto = mock(FeedbackReviewRequestDto.class);

		User user = mock(User.class);
		User tutor = mock(User.class);
		User otherUser = mock(User.class);
		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);

		Feedback feedback = mock(Feedback.class);
		FeedbackRequestEntity requestEntity = mock(FeedbackRequestEntity.class);
		when(feedBackRepository.findWithRequestAndUserById(feedbackId)).thenReturn(Optional.of(feedback));
		when(feedback.getFeedbackRequestEntity()).thenReturn(requestEntity);
		when(feedback.getTutor()).thenReturn(tutor);
		when(requestEntity.getUser()).thenReturn(otherUser);
		when(otherUser.getUserId()).thenReturn(999L);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.createReview(userId, feedbackId, dto)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}
	@Test
	public void 리뷰_수정_테스트() {
		Long studentId = 1L;
		Long reviewId = 1L;

		User student = mock(User.class);
		FeedbackReview feedbackReview = mock(FeedbackReview.class);
		FeedbackReviewRequestDto feedbackReviewRequestDto = mock(FeedbackReviewRequestDto.class);

		when(userRepository.findByIdOrElseThrow(studentId)).thenReturn(student);

		when(feedbackReview.getId()).thenReturn(reviewId);
		when(feedbackReview.getUserId()).thenReturn(studentId);
		when(feedBackReviewRepository.save(any())).thenReturn(feedbackReview);
		when(feedBackReviewRepository.findByIdOrElseThrow(1L)).thenReturn(feedbackReview);
		when(feedbackReview.getModifiedAt()).thenReturn(LocalDateTime.now());

		assertDoesNotThrow(() -> feedbackReviewService.updateReview(studentId, reviewId, feedbackReviewRequestDto));
		verify(feedBackReviewRepository, times(1)).save(any());
	}
	@Test
	void 리뷰수정_실패_존재하지않는유저() {
		Long userId = 1L;
		Long reviewId = 2L;
		FeedbackReviewRequestDto dto = mock(FeedbackReviewRequestDto.class);

		when(userRepository.findByIdOrElseThrow(userId))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.updateReview(userId, reviewId, dto)
		);

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 리뷰수정_실패_존재하지않는리뷰() {
		Long userId = 1L;
		Long reviewId = 2L;
		FeedbackReviewRequestDto dto = mock(FeedbackReviewRequestDto.class);

		User user = mock(User.class);
		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);

		when(feedBackReviewRepository.findByIdOrElseThrow(reviewId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_FEEDBACK_REVIEW));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.updateReview(userId, reviewId, dto)
		);

		assertEquals(ErrorCode.NOT_FOUND_FEEDBACK_REVIEW, exception.getErrorCode());
	}

	@Test
	void 리뷰수정_실패_작성자가아님() {
		Long userId = 1L;
		Long reviewId = 2L;
		FeedbackReviewRequestDto dto = mock(FeedbackReviewRequestDto.class);

		User user = mock(User.class);
		FeedbackReview review = mock(FeedbackReview.class);

		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(feedBackReviewRepository.findByIdOrElseThrow(reviewId)).thenReturn(review);
		when(review.getUserId()).thenReturn(999L);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.updateReview(userId, reviewId, dto)
		);

		assertEquals(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS, exception.getErrorCode());
	}
	@Test
	public void 리뷰_삭제_테스트() {
		Long studentId = 1L;
		Long reviewId = 1L;

		User student = mock(User.class);
		FeedbackReview feedbackReview = mock(FeedbackReview.class);
		FeedbackReviewRequestDto feedbackReviewRequestDto = mock(FeedbackReviewRequestDto.class);

		when(userRepository.findByIdOrElseThrow(studentId)).thenReturn(student);

		when(feedbackReview.getUserId()).thenReturn(studentId);
		when(feedBackReviewRepository.findByIdOrElseThrow(1L)).thenReturn(feedbackReview);

		assertDoesNotThrow(() -> feedbackReviewService.deleteReview(studentId, reviewId));
	}
	@Test
	void 리뷰삭제_실패_존재하지않는유저() {
		Long userId = 1L;
		Long reviewId = 2L;

		when(userRepository.findByIdOrElseThrow(userId))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.deleteReview(userId, reviewId)
		);

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 리뷰삭제_실패_존재하지않는리뷰() {
		Long userId = 1L;
		Long reviewId = 2L;

		User user = mock(User.class);
		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);

		when(feedBackReviewRepository.findByIdOrElseThrow(reviewId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_FEEDBACK_REVIEW));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.deleteReview(userId, reviewId)
		);

		assertEquals(ErrorCode.NOT_FOUND_FEEDBACK_REVIEW, exception.getErrorCode());
	}

	@Test
	void 리뷰삭제_실패_작성자가아님() {
		Long userId = 1L;
		Long reviewId = 2L;

		User user = mock(User.class);
		FeedbackReview review = mock(FeedbackReview.class);

		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(feedBackReviewRepository.findByIdOrElseThrow(reviewId)).thenReturn(review);
		when(review.getUserId()).thenReturn(999L);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.deleteReview(userId, reviewId)
		);

		assertEquals(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS, exception.getErrorCode());
	}
	@Test
	public void 학생_기준_리뷰_유저_단건_조회_테스트() {
		Long studentId = 1L;
		Long tutor1Id = 2L;
		User student = mock(User.class);
		when(userRepository.findByIdOrElseThrow(studentId)).thenReturn(student);
		when(student.getRole()).thenReturn(UserRole.STUDENT);

		FeedbackReview feedbackReview = mock(FeedbackReview.class);
		when(feedbackReview.getId()).thenReturn(1L);
		when(feedbackReview.getUserId()).thenReturn(studentId);
		when(feedbackReview.getTutorId()).thenReturn(tutor1Id);
		when(feedbackReview.getContent()).thenReturn("학생이 리뷰를 달았습니다.");
		when(feedbackReview.getRating()).thenReturn(3);
		when(feedbackReview.getModifiedAt()).thenReturn(LocalDateTime.now());
		when(feedBackReviewRepository.findByIdOrElseThrow(1L)).thenReturn(feedbackReview);

		FeedbackReviewDetailsDto feedbackReviewDetailsDto
			= feedbackReviewService.getReview(studentId, 1L);

		assertNotNull(feedbackReviewDetailsDto);
		assertEquals(1L, feedbackReviewDetailsDto.getId());
		assertEquals(1L, feedbackReviewDetailsDto.getUserId());
		assertEquals(2L, feedbackReviewDetailsDto.getTutorId());
	}

	@Test
	public void 튜터_기준_리뷰_유저_단건_조회_테스트() {

		Long tutorId = 3L;
		Long studentId = 1L;
		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(tutorId)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);

		FeedbackReview feedbackReview = mock(FeedbackReview.class);
		when(feedbackReview.getId()).thenReturn(1L);
		when(feedbackReview.getUserId()).thenReturn(studentId);
		when(feedbackReview.getTutorId()).thenReturn(tutorId);
		when(feedbackReview.getContent()).thenReturn("학생이 리뷰를 달았습니다.");
		when(feedbackReview.getRating()).thenReturn(3);
		when(feedbackReview.getModifiedAt()).thenReturn(LocalDateTime.now());
		when(feedBackReviewRepository.findByIdOrElseThrow(1L)).thenReturn(feedbackReview);

		FeedbackReviewDetailsDto feedbackReviewDetailsDto
			= feedbackReviewService.getReview(tutorId, 1L);

		assertNotNull(feedbackReviewDetailsDto);
		assertEquals(1L, feedbackReviewDetailsDto.getId());
		assertEquals(1L, feedbackReviewDetailsDto.getUserId());
		assertEquals(3L, feedbackReviewDetailsDto.getTutorId());
	}
	@Test
	void 리뷰단건조회_실패_존재하지않는유저() {
		Long userId = 1L;
		Long reviewId = 2L;

		when(userRepository.findByIdOrElseThrow(userId))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.getReview(userId, reviewId)
		);

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 리뷰단건조회_실패_존재하지않는리뷰() {
		Long userId = 1L;
		Long reviewId = 2L;

		User user = mock(User.class);
		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);

		when(feedBackReviewRepository.findByIdOrElseThrow(reviewId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_FEEDBACK_REVIEW));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.getReview(userId, reviewId)
		);

		assertEquals(ErrorCode.NOT_FOUND_FEEDBACK_REVIEW, exception.getErrorCode());
	}

	@Test
	void 리뷰단건조회_실패_권한없음() {
		Long userId = 1L;
		Long reviewId = 2L;

		User user = mock(User.class);
		FeedbackReview review = mock(FeedbackReview.class);

		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(feedBackReviewRepository.findByIdOrElseThrow(reviewId)).thenReturn(review);
		when(user.getRole()).thenReturn(UserRole.STUDENT);
		when(review.getUserId()).thenReturn(999L);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackReviewService.getReview(userId, reviewId)
		);

		assertEquals(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS, exception.getErrorCode());
	}

	@Test
	public void 학생_기준_리뷰_유저_다건_조회_테스트() {

		Long studentId = 1L;
		Long tutor1Id = 2L;
		User student = mock(User.class);
		when(userRepository.findByIdOrElseThrow(studentId)).thenReturn(student);
		when(student.getUserId()).thenReturn(studentId);
		when(student.getRole()).thenReturn(UserRole.STUDENT);

		PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		FeedbackReview feedbackReview = mock(FeedbackReview.class);
		when(feedbackReview.getId()).thenReturn(1L);
		when(feedbackReview.getUserId()).thenReturn(studentId);
		when(feedbackReview.getTutorId()).thenReturn(tutor1Id);
		when(feedbackReview.getRating()).thenReturn(3);
		when(feedbackReview.getModifiedAt()).thenReturn(LocalDateTime.now());

		Page<FeedbackReview> page = new PageImpl<>(List.of(feedbackReview));
		when(feedBackReviewRepository.findByUserIdAndDeletedAtIsNull(studentId, pageable)).thenReturn(page);

		List<FeedbackReviewListDto> feedbackReviewDetailsDto
			= feedbackReviewService.getReviews(studentId, 0, 10);

		assertNotNull(feedbackReviewDetailsDto);
		assertEquals(1L, feedbackReviewDetailsDto.get(0).getId());
		assertEquals(1L, feedbackReviewDetailsDto.get(0).getUserId());
		assertEquals(2L, feedbackReviewDetailsDto.get(0).getTutorId());
	}

	@Test
	public void 튜터_기준_리뷰_유저_다건_조회_테스트() {

		Long studentId = 1L;
		Long tutorId = 2L;

		User student = mock(User.class);
		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(eq(tutorId))).thenReturn(tutor);
		when(tutor.getUserId()).thenReturn(tutorId);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);

		PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		FeedbackReview feedbackReview = mock(FeedbackReview.class);
		when(feedbackReview.getId()).thenReturn(1L);
		when(feedbackReview.getUserId()).thenReturn(studentId);
		when(feedbackReview.getTutorId()).thenReturn(tutorId);
		when(feedbackReview.getRating()).thenReturn(3);
		when(feedbackReview.getModifiedAt()).thenReturn(LocalDateTime.now());

		Page<FeedbackReview> page = new PageImpl<>(List.of(feedbackReview));
		when(feedBackReviewRepository.findByTutorIdAndDeletedAtIsNull(tutorId, pageable)).thenReturn(page);

		List<FeedbackReviewListDto> feedbackReviewDetailsDto
			= feedbackReviewService.getReviews(tutorId, 0, 10);

		assertNotNull(feedbackReviewDetailsDto);
		assertEquals(1L, feedbackReviewDetailsDto.get(0).getId());
		assertEquals(1L, feedbackReviewDetailsDto.get(0).getUserId());
		assertEquals(2L, feedbackReviewDetailsDto.get(0).getTutorId());
	}


	@Test
	public void 평점_출력 () {
		Long tutorId = 2L;
		User tutor  = mock(User.class);
		when(userRepository.findByIdOrElseThrow(tutorId)).thenReturn(tutor);
		when(tutor.getUserId()).thenReturn(tutorId);
		when(feedBackReviewRepository.getAverageRating(tutorId)).thenReturn(null);

		Double avg = feedbackReviewService.getAverageRating(tutorId);

		assertNotNull(avg);
		assertEquals(0.0, avg);
	}


}
