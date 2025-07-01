package com.example.feedprep.domain.feedbackrequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
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
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRejectRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.feedbackrequestentity.repository.FeedbackRequestEntityRepository;
import com.example.feedprep.domain.feedbackrequestentity.service.FeedbackRequestServiceImpl;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class FeedbackRequestServiceTest {
	@Mock
	private FeedbackRequestEntityRepository feedbackRequestEntityRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private DocumentRepository documentRepository;
	@InjectMocks
	private FeedbackRequestServiceImpl feedbackRequestService;

	@Test
	void 피드백신청_실패_존재하지않는유저() {
		Long userId = 1L;
		FeedbackRequestDto dto = mock(FeedbackRequestDto.class);

		when(userRepository.findByIdOrElseThrow(userId))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException exception = assertThrows(CustomException.class,
			() -> feedbackRequestService.createRequest(userId, dto));

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 피드백신청_실패_존재하지않는튜터() {
		Long userId = 1L;
		Long tutorId = 2L;
		FeedbackRequestDto dto = mock(FeedbackRequestDto.class);

		User user = mock(User.class);
		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(dto.getTutorId()).thenReturn(tutorId);

		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_TUTOR));

		CustomException exception = assertThrows(CustomException.class,
			() -> feedbackRequestService.createRequest(userId, dto));

		assertEquals(ErrorCode.NOT_FOUND_TUTOR, exception.getErrorCode());
	}

	@Test
	void 피드백신청_실패_튜터승인되지않음() {
		Long userId = 1L;
		Long tutorId = 2L;
		FeedbackRequestDto dto = mock(FeedbackRequestDto.class);

		User user = mock(User.class);
		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(dto.getTutorId()).thenReturn(tutorId);
		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.STUDENT); // 승인되지 않은 상태


		CustomException exception = assertThrows(CustomException.class,
			() -> feedbackRequestService.createRequest(userId, dto));

		assertEquals(ErrorCode.PENDING_TUTOR, exception.getErrorCode());
	}

	@Test
	void 피드백신청_실패_존재하지않는문서() {
		Long userId = 1L;
		Long tutorId = 2L;
		Long documentId = 3L;
		FeedbackRequestDto dto = mock(FeedbackRequestDto.class);

		User user = mock(User.class);
		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(dto.getTutorId()).thenReturn(tutorId);
		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);
		when(dto.getDocumentId()).thenReturn(documentId);

		when(documentRepository.findByIdOrElseThrow(documentId))
			.thenThrow(new CustomException(ErrorCode.INVALID_DOCUMENT));

		CustomException exception = assertThrows(CustomException.class,
			() -> feedbackRequestService.createRequest(userId, dto));

		assertEquals(ErrorCode.INVALID_DOCUMENT, exception.getErrorCode());
	}

	@Test
	void 피드백신청_실패_중복_피드백_요청_존재() {
		Long userId = 1L;
		Long tutorId = 2L;
		Long documentId = 3L;
		FeedbackRequestDto dto = mock(FeedbackRequestDto.class);

		User user = mock(User.class);
		User tutor = mock(User.class);
		Document document = mock(Document.class);
		FeedbackRequestEntity existingRequest = mock(FeedbackRequestEntity.class);

		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(dto.getTutorId()).thenReturn(tutorId);
		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);
		when(tutor.getUserId()).thenReturn(tutorId);
		when(dto.getDocumentId()).thenReturn(documentId);
		when(documentRepository.findByIdOrElseThrow(documentId)).thenReturn(document);

		when(feedbackRequestEntityRepository.findTop1ByUser_UserIdAndTutor_UserIdAndRequestState(
			eq(userId), eq(tutorId), eq(RequestState.PENDING))
		).thenReturn(Optional.of(existingRequest));

		CustomException exception = assertThrows(CustomException.class,
			() -> feedbackRequestService.createRequest(userId, dto)
		);

		assertEquals(ErrorCode.DUPLICATE_FEEDBACK_REQUEST, exception.getErrorCode());
	}
	@Test
	void 피드백신청_수정실패_존재하지않는요청() {
		Long userId = 1L;
		Long requestId = 2L;
		FeedbackRequestDto dto = mock(FeedbackRequestDto.class);

		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_FEEDBACK_REQUEST));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.updateRequest(userId, requestId, dto)
		);

		assertEquals(ErrorCode.NOT_FOUND_FEEDBACK_REQUEST, exception.getErrorCode());
	}

	@Test
	void 피드백신청_수정실패_권한없음() {
		Long userId = 1L;
		Long requestId = 2L;
		FeedbackRequestDto dto = mock(FeedbackRequestDto.class);

		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);
		User otherUser = mock(User.class);

		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);
		when(entity.getUser()).thenReturn(otherUser);
		when(otherUser.getUserId()).thenReturn(999L);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.updateRequest(userId, requestId, dto)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}

	@Test
	void 피드백신청_수정실패_완료상태수정불가() {
		Long userId = 1L;
		Long requestId = 2L;
		FeedbackRequestDto dto = mock(FeedbackRequestDto.class);

		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);
		User user = mock(User.class);

		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);
		when(entity.getUser()).thenReturn(user);
		when(user.getUserId()).thenReturn(userId);
		when(entity.getRequestState()).thenReturn(RequestState.COMPLETED);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.updateRequest(userId, requestId, dto)
		);

		assertEquals(ErrorCode.CANNOT_EDIT_NON_EDITABLE_REQUEST, exception.getErrorCode());
	}
	@Test
	void 피드백신청_취소실패_존재하지않는요청() {
		Long userId = 1L;
		Long requestId = 2L;

		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_FEEDBACK_REQUEST));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.cancelRequest(userId, requestId)
		);

		assertEquals(ErrorCode.NOT_FOUND_FEEDBACK_REQUEST, exception.getErrorCode());
	}

	@Test
	void 피드백신청_취소실패_권한없음() {
		Long userId = 1L;
		Long requestId = 2L;

		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);
		User otherUser = mock(User.class);

		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);
		when(entity.getUser()).thenReturn(otherUser);
		when(otherUser.getUserId()).thenReturn(999L);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.cancelRequest(userId, requestId)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}

	@Test
	void 피드백신청_취소실패_완료상태취소불가() {
		Long userId = 1L;
		Long requestId = 2L;

		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);
		User user = mock(User.class);

		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);
		when(entity.getUser()).thenReturn(user);
		when(user.getUserId()).thenReturn(userId);
		when(entity.getRequestState()).thenReturn(RequestState.COMPLETED);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.cancelRequest(userId, requestId)
		);

		assertEquals(ErrorCode.CANNOT_EDIT_COMPLETED_REQUEST, exception.getErrorCode());
	}
	@Test
	void 피드백신청_단건조회_실패_유저없음() {
		Long userId = 1L;
		Long requestId = 2L;

		when(userRepository.findByIdOrElseThrow(userId))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.getFeedbackRequest(userId, requestId)
		);

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 피드백신청_단건조회_실패_요청없음() {
		Long userId = 1L;
		Long requestId = 2L;
		User user = mock(User.class);

		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_FEEDBACK_REQUEST));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.getFeedbackRequest(userId, requestId)
		);

		assertEquals(ErrorCode.NOT_FOUND_FEEDBACK_REQUEST, exception.getErrorCode());
	}

	@Test
	void 피드백신청_단건조회_실패_권한없음_학생() {
		Long userId = 1L;
		Long tutorId = 2L;
		Long requestId = 1L;

		User user = mock(User.class);
		User tutor = mock(User.class);
		User requestUser = mock(User.class);
		User requestTutor = mock(User.class);
		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);

		when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);
		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);

		when(user.getRole()).thenReturn(UserRole.STUDENT);
		when(user.getUserId()).thenReturn(tutorId);

		when(entity.getUser()).thenReturn(requestUser);
		when(entity.getTutor()).thenReturn(requestTutor);
		when(requestUser.getUserId()).thenReturn(99L);      // 요청자가 userId
		when(requestTutor.getUserId()).thenReturn(tutorId);       // 다른 tutorId

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.getFeedbackRequest(userId, requestId)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}

	@Test
	void 피드백신청_단건조회_실패_권한없음_튜터() {
		Long userId = 1L;
		Long tutorId = 2L;
		Long requestId = 1L;

		User user = mock(User.class);
		User tutor = mock(User.class);
		User requestUser = mock(User.class);
		User requestTutor = mock(User.class);
		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);

		when(userRepository.findByIdOrElseThrow(tutorId)).thenReturn(tutor);
		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);

		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);
		when(tutor.getUserId()).thenReturn(tutorId);

		when(entity.getUser()).thenReturn(requestUser);
		when(entity.getTutor()).thenReturn(requestTutor);
		when(requestUser.getUserId()).thenReturn(userId);      // 요청자가 userId
		when(requestTutor.getUserId()).thenReturn(999L);       // 다른 tutorId

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.getFeedbackRequest(tutorId, requestId)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}
	@Test
	void 피드백신청_수락_실패_튜터없음() {
		Long tutorId = 1L;
		Long requestId = 2L;

		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_TUTOR));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.acceptRequest(tutorId, requestId)
		);

		assertEquals(ErrorCode.NOT_FOUND_TUTOR, exception.getErrorCode());
	}

	@Test
	void 피드백신청_수락_실패_튜터승인되지않음() {
		Long tutorId = 1L;
		Long requestId = 2L;

		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.STUDENT);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.acceptRequest(tutorId, requestId)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}

	@Test
	void 피드백신청_수락_실패_진행중아님() {
		Long tutorId = 1L;
		Long requestId = 2L;

		User tutor = mock(User.class);
		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);

		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);
		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);
		when(entity.getRequestState()).thenReturn(RequestState.COMPLETED);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.acceptRequest(tutorId, requestId)
		);

		assertEquals(ErrorCode.CANNOT_REJECT_NON_PENDING_FEEDBACK, exception.getErrorCode());
	}
	@Test
	void 피드백신청_거절_실패_튜터없음() {
		Long tutorId = 1L;
		Long requestId = 2L;
		FeedbackRejectRequestDto dto = new FeedbackRejectRequestDto("기타");

		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_TUTOR));

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.rejectFeedbackRequest(tutorId, requestId, 1, dto)
		);

		assertEquals(ErrorCode.NOT_FOUND_TUTOR, exception.getErrorCode());
	}

	@Test
	void 피드백신청_거절_실패_튜터승인되지않음() {
		Long tutorId = 1L;
		Long requestId = 2L;
		FeedbackRejectRequestDto dto = new FeedbackRejectRequestDto("기타");

		User tutor = mock(User.class);
		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.STUDENT);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.rejectFeedbackRequest(tutorId, requestId, 1, dto)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}

	@Test
	void 피드백신청_거절_실패_진행중아님() {
		Long tutorId = 1L;
		Long requestId = 2L;
		FeedbackRejectRequestDto dto = new FeedbackRejectRequestDto("기타");

		User tutor = mock(User.class);
		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);

		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);
		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);
		when(entity.getRequestState()).thenReturn(RequestState.COMPLETED);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.rejectFeedbackRequest(tutorId, requestId, 1, dto)
		);

		assertEquals(ErrorCode.CANNOT_REJECT_NON_PENDING_FEEDBACK, exception.getErrorCode());
	}

	@Test
	void 피드백신청_거절_실패_본인에게신청아님() {
		Long tutorId = 1L;
		Long requestId = 2L;
		FeedbackRejectRequestDto dto = new FeedbackRejectRequestDto("기타");

		User tutor = mock(User.class);
		FeedbackRequestEntity entity = mock(FeedbackRequestEntity.class);
		User otherTutor = mock(User.class);

		when(userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR)).thenReturn(tutor);
		when(tutor.getRole()).thenReturn(UserRole.APPROVED_TUTOR);
		when(feedbackRequestEntityRepository.findByIdOrElseThrow(requestId)).thenReturn(entity);
		when(entity.getRequestState()).thenReturn(RequestState.PENDING);
		when(entity.getTutor()).thenReturn(otherTutor);
		when(otherTutor.getUserId()).thenReturn(999L);

		CustomException exception = assertThrows(CustomException.class, () ->
			feedbackRequestService.rejectFeedbackRequest(tutorId, requestId, 1, dto)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS, exception.getErrorCode());
	}
}
