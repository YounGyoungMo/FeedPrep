package com.example.feedprep.domain.feedback.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.feedback.dto.request.FeedbackWriteRequestDto;
import com.example.feedprep.domain.feedback.dto.response.FeedbackResponseDto;
import com.example.feedprep.domain.feedback.entity.Feedback;
import com.example.feedprep.domain.feedback.repository.FeedBackRepository;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.feedbackrequestentity.repository.FeedbackRequestEntityRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService{
	private final FeedBackRepository feedBackRepository;
	private final FeedbackRequestEntityRepository feedbackRequestEntityRepository;
	private final UserRepository userRepository;



	@Transactional
	@Override
	public FeedbackResponseDto createFeedback(Long tutorId, Long requestId, FeedbackWriteRequestDto dto) {
		// 1. 유효성 검사 (필수 필드, 글자 수 등)
		// 3. 튜터 권한 확인
		User tutor = userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR);
		if(!tutor.getRole().equals(UserRole.APPROVED_TUTOR)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		// 2. 피드백 요청 상태 확인 (이미 완료/거절된 요청인지)
		FeedbackRequestEntity request = feedbackRequestEntityRepository.findPendingByIdAndTutorOrElseThrow(
			tutor.getUserId(), requestId, ErrorCode.USER_NOT_FOUND);

		if(!request.getRequestState().equals(RequestState.IN_PROGRESS)){
			throw new CustomException(ErrorCode.INVALID_REQUEST_STATE);
		}
		// 4. Feedback 엔티티 생성
		Feedback feedback = new Feedback(dto, tutor);
		request.updateRequestState(RequestState.COMPLETED);
		feedback.updateFeedbackRequest(request);
		// 6. 저장 (트랜잭션 내에서)
		Feedback saveFeedback = feedBackRepository.save(feedback);
		// 7. 알림 or 이벤트 발행 (선택)

		// 8. Response 반환
		return new FeedbackResponseDto(saveFeedback);
	}

	@Transactional
	@Override
	public FeedbackResponseDto updateFeedback(Long tutorId, Long feedbackId, FeedbackWriteRequestDto dto) {
		// 1. 튜터 본인 확인
		User tutor = userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR);
		if(!tutor.getRole().equals(UserRole.APPROVED_TUTOR)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}

		// 2. 피드백 존재 여부 확인
		Feedback feedback = feedBackRepository.findById(feedbackId)
			.orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEEDBACK));
		// 3. 본인 피드백인지 검사
		if(feedback.getFeedbackRequestEntity().getRequestState().equals(RequestState.PENDING)){
			throw new CustomException(ErrorCode.CANNOT_EDIT_PENDING_FEEDBACK);
		}
		if(!feedback.getTutor().getUserId().equals(tutor.getUserId())){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		// 4. dto 내용으로 feedback 업데이트
		feedback.updateFeedback(dto);
		// 5. 저장 (save)
		Feedback saveFeedback = feedBackRepository.save(feedback);
		// 6. 알림/이벤트 (선택)
		// 7. 알림 or 이벤트 발행 (선택)
		// 8. Response 반환
		return new FeedbackResponseDto(saveFeedback);
	}


}
