package com.example.feedprep.domain.feedbackreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.example.feedprep.domain.feedback.entity.Feedback;
import com.example.feedprep.domain.feedback.repository.FeedBackRepository;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewRequestDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewDetailsDto;
import com.example.feedprep.domain.feedbackreview.entity.FeedbackReview;
import com.example.feedprep.domain.feedbackreview.repository.FeedBackReviewRepository;
import com.example.feedprep.domain.feedbackreview.service.FeedbackReviewServiceImpl;
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

	@InjectMocks
	private FeedbackReviewServiceImpl feedbackReviewService;

	@Test
	public void 리뷰_생성_테스트() {

		Long studentId = 1L;

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
		verify(feedBackReviewRepository, times(1)).save(any());
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
		when(feedbackReview.getContent()).thenReturn("학생이 리뷰를 달았습니다.");
		when(feedbackReview.getRating()).thenReturn(3);
		when(feedbackReview.getModifiedAt()).thenReturn(LocalDateTime.now());

		Page<FeedbackReview> page = new PageImpl<>(List.of(feedbackReview));
		when(feedBackReviewRepository.findByUserIdAndDeletedAtIsNull(studentId, pageable)).thenReturn(page);

		List<FeedbackReviewDetailsDto> feedbackReviewDetailsDto
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
		when(feedbackReview.getContent()).thenReturn("학생이 쓴 리뷰를 화인합니다.");
		when(feedbackReview.getRating()).thenReturn(3);
		when(feedbackReview.getModifiedAt()).thenReturn(LocalDateTime.now());

		Page<FeedbackReview> page = new PageImpl<>(List.of(feedbackReview));
		when(feedBackReviewRepository.findByTutorIdAndDeletedAtIsNull(tutorId, pageable)).thenReturn(page);

		List<FeedbackReviewDetailsDto> feedbackReviewDetailsDto
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
