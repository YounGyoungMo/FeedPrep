package com.example.feedprep.domain.feedback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.feedback.dto.request.FeedbackWriteRequestDto;
import com.example.feedprep.domain.feedback.entity.Feedback;
import com.example.feedprep.domain.feedback.repository.FeedBackRepository;
import com.example.feedprep.domain.feedback.service.FeedbackServiceImpl;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.feedbackrequestentity.repository.FeedbackRequestEntityRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {
	@InjectMocks
	private FeedbackServiceImpl feedbackService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private FeedbackRequestEntityRepository feedbackRequestEntityRepository;
	@Mock
	private FeedBackRepository feedbackRepository;

	@Test
	void 피드백_생성_실패_중복_작성_시_예외_발생() {
		// given
		Long tutorId = 1L;
		Long requestId = 1L;
		FeedbackWriteRequestDto dto = mock(FeedbackWriteRequestDto.class);

		User tutor = mock(User.class);
		given(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR))
			.willReturn(tutor);

		FeedbackRequestEntity requestEntity = mock(FeedbackRequestEntity.class);

		// 중복 피드백 존재하도록 설정
		given(feedbackRepository.existsFeedbackByFeedbackRequestEntityIdAndTutorId(requestId, tutorId))
			.willReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> feedbackService.createFeedback(tutorId, requestId, dto)
		);

		assertEquals(ErrorCode.DUPLICATE_FEEDBACK, exception.getErrorCode());
		assertEquals("이미 동일한 내용으로 작성하였습니다.", exception.getMessage());
	}

	@Test
	void 피드백_작성_추가_존재하지_않는_튜터일때_경우_예외_발생() {
		Long tutorId = 1L;
		Long requestId = 1L;
		FeedbackWriteRequestDto dto = new FeedbackWriteRequestDto("내용");

		when(userRepository.findByIdOrElseThrow(eq(tutorId), any()))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_TUTOR));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackService.createFeedback(tutorId, requestId, dto)
		);

		assertEquals(ErrorCode.NOT_FOUND_TUTOR, exception.getErrorCode());
	}

	@Test
	void 피드백_작성_추가_승인되지_않는_튜터일_경우_예외_발생() {
		Long tutorId = 1L;
		Long requestId = 1L;
		FeedbackWriteRequestDto dto = new FeedbackWriteRequestDto("내용");

		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(eq(tutorId), any())).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.STUDENT);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackService.createFeedback(tutorId, requestId, dto)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}

	@Test
	void  피드백_작성_추가_존재하지_않는_피드백_요청일_경우_예외_발생() {
		Long tutorId = 1L;
		Long requestId = 1L;
		FeedbackWriteRequestDto dto = new FeedbackWriteRequestDto("내용");

		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(eq(tutorId), any())).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);

		when(feedbackRequestEntityRepository.findPendingByIdAndTutorOrElseThrow(anyLong(), anyLong(), any()))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackService.createFeedback(tutorId, requestId, dto)
		);

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 피드백_작성_추가_피드백_요청_상태가_진행중이_아닐_경우_예외_발생() {
		Long tutorId = 1L;
		Long userId = 2L;
		Long requestId = 1L;
		FeedbackWriteRequestDto dto = new FeedbackWriteRequestDto("내용");

		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getUserId()).thenReturn(tutorId);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);
		User user = mock(User.class);

		FeedbackRequestEntity requestEntity = mock(FeedbackRequestEntity.class);
		when(requestEntity.getUser()).thenReturn(user);
		when(feedbackRequestEntityRepository.findPendingByIdAndTutorOrElseThrow(
			eq(tutorId),
			eq(requestId),
			eq(ErrorCode.USER_NOT_FOUND)))
			.thenReturn(requestEntity);
		when(requestEntity.getRequestState()).thenReturn(RequestState.COMPLETED);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackService.createFeedback(tutorId, requestId, dto)
		);

		assertEquals(ErrorCode.INVALID_REQUEST_STATE, exception.getErrorCode());
	}
	@Test
	void 피드백_수정_존재하지_않는_튜터일_경우_예외_발생() {
		Long tutorId = 1L;
		Long feedbackId = 1L;
		FeedbackWriteRequestDto dto = new FeedbackWriteRequestDto("수정된 내용");

		when(userRepository.findByIdOrElseThrow(eq(tutorId), any()))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_TUTOR));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackService.updateFeedback(tutorId, feedbackId, dto)
		);

		assertEquals(ErrorCode.NOT_FOUND_TUTOR, exception.getErrorCode());
	}

	@Test
	void 피드백_수정_승인되지_않은_튜터일_경우_예외_발생() {
		Long tutorId = 1L;
		Long feedbackId = 1L;
		FeedbackWriteRequestDto dto = new FeedbackWriteRequestDto("수정된 내용");

		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(eq(tutorId), any())).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.PENDING_TUTOR);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackService.updateFeedback(tutorId, feedbackId, dto)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}

	@Test
	void 피드백_수정_존재하지_않는_피드백일_경우_예외_발생() {
		Long tutorId = 1L;
		Long feedbackId = 1L;
		FeedbackWriteRequestDto dto = new FeedbackWriteRequestDto("수정된 내용");

		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(eq(tutorId), any())).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);

		when(feedbackRepository.findById(feedbackId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_FEEDBACK));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackService.updateFeedback(tutorId, feedbackId, dto)
		);

		assertEquals(ErrorCode.NOT_FOUND_FEEDBACK, exception.getErrorCode());
	}

	@Test
	void 피드백_수정_튜터_ID와_피드백의_튜터_ID가_일치하지_않을_경우_예외_발생() {
		Long tutorId = 1L;
		Long feedbackId = 1L;
		FeedbackWriteRequestDto dto = new FeedbackWriteRequestDto("수정된 내용");

		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(eq(tutorId), any())).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);

		Feedback feedback = mock(Feedback.class);
		FeedbackRequestEntity requestEntity = mock(FeedbackRequestEntity.class);
		when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

		User feedbackTutor = mock(User.class);
		when(feedback.getTutor()).thenReturn(feedbackTutor);
		when(feedbackTutor.getUserId()).thenReturn(999L); // tutorId와 불일치

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackService.updateFeedback(tutorId, feedbackId, dto)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}
}
