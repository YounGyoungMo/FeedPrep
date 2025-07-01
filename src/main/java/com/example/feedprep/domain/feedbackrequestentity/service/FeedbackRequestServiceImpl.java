package com.example.feedprep.domain.feedbackrequestentity.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.feedbackrequestentity.common.RejectReason;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRejectRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.TutorFeedbackResponseDetailsDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.UserFeedbackRequestDetailsDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.FeedbackRequestDetailsDto;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.feedbackrequestentity.repository.FeedbackRequestEntityRepository;
import com.example.feedprep.domain.notification.service.NotificationServiceImpl;
import com.example.feedprep.domain.point.service.PointServiceImpl;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Repository
@RequiredArgsConstructor
public class FeedbackRequestServiceImpl implements FeedbackRequestService {
	private final FeedbackRequestEntityRepository feedbackRequestEntityRepository;
	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;
	private final NotificationServiceImpl notificationService;
	private final PointServiceImpl pointService;

	@Transactional
	@Override
	public UserFeedbackRequestDetailsDto createRequest(Long userId, FeedbackRequestDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		User tutor = userRepository.findByIdOrElseThrow(dto.getTutorId(), ErrorCode.NOT_FOUND_TUTOR);
        if(!tutor.getRole().equals(UserRole.APPROVED_TUTOR)){
			throw new CustomException(ErrorCode.PENDING_TUTOR);
		}

		Document document = documentRepository.findByIdOrElseThrow(dto.getDocumentId());

        FeedbackRequestEntity feedbackRequestEntity =
			 feedbackRequestEntityRepository.findTop1ByUser_UserIdAndTutor_UserIdAndRequestState(
				 userId,
				 tutor.getUserId(),
				 RequestState.PENDING)
				 .orElse(null);

        if(feedbackRequestEntity != null){
			throw new CustomException(ErrorCode.DUPLICATE_FEEDBACK_REQUEST);
		}

		FeedbackRequestEntity request = new FeedbackRequestEntity(dto, user, tutor, document);
		request.updateRequestState(RequestState.PENDING);
		FeedbackRequestEntity getInfoRequest =feedbackRequestEntityRepository.save(request);

		pointService.makePayment(getInfoRequest);

		notificationService.sendNotification(userId, tutor.getUserId(), 101);

		return new UserFeedbackRequestDetailsDto(getInfoRequest);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserFeedbackRequestDetailsDto> getRequests(
		Long userId,         // 신청자
		Long tutorId,        // 피드백 받을 대상
		Long documentId,     // 문서
		LocalDateTime month, // 월별 필터
		RequestState requestState,
		int page,
		int size
	)
	{
		//유저 본인 확인
		User user = userRepository.findByIdOrElseThrow(userId);
		if(!user.getUserId().equals(userId)){
			throw  new CustomException(ErrorCode. UNAUTHORIZED_REQUESTER_ACCESS);
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<FeedbackRequestEntity> pages =
			feedbackRequestEntityRepository.findByRequest(userId, tutorId, documentId,month, requestState, pageRequest);

		return pages.stream().map(UserFeedbackRequestDetailsDto::new).toList();
	}

	@Transactional
	@Override
	public UserFeedbackRequestDetailsDto updateRequest(Long userId, Long feedbackRequestId, FeedbackRequestDto dto) {

		//요청이 존재하는 가?
		FeedbackRequestEntity request = feedbackRequestEntityRepository.findByIdOrElseThrow(feedbackRequestId);
		if(!request.getUser().getUserId().equals(userId)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		if (request.getRequestState() != RequestState.PENDING) {
			throw new CustomException(ErrorCode.CANNOT_EDIT_NON_EDITABLE_REQUEST);
		}

		User tutor = userRepository.findByIdOrElseThrow(dto.getTutorId());
		Document document = documentRepository.findByIdOrElseThrow(dto.getDocumentId());

		request.updateFeedbackRequestEntity(dto, tutor, document);
		FeedbackRequestEntity getInfoRequest =feedbackRequestEntityRepository.save(request);
		return new UserFeedbackRequestDetailsDto(getInfoRequest);
	}

	@Transactional
	@Override
	public UserFeedbackRequestDetailsDto cancelRequest(Long userId, Long feedbackRequestId) {
		//요청이 존재하는 가?
		FeedbackRequestEntity request = feedbackRequestEntityRepository.findByIdOrElseThrow(feedbackRequestId);
		if(!request.getUser().getUserId().equals(userId))
		{
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		if (request.getRequestState() != RequestState.PENDING){
			throw new CustomException(ErrorCode.CANNOT_EDIT_COMPLETED_REQUEST);
		}

		request.updateRequestState(RequestState.CANCELED);
		FeedbackRequestEntity getInfoRequest =feedbackRequestEntityRepository.save(request);
		pointService.refundPoint(getInfoRequest);

		Map<String, Object> data =  new LinkedHashMap<>();
		data.put("modifiedAt ", request.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		return new UserFeedbackRequestDetailsDto( getInfoRequest);
	}


	@Transactional(readOnly = true)
	@Override
	public FeedbackRequestDetailsDto getFeedbackRequest(Long userId, Long feedbackRequestId) {
		//유저 확인.
		User user= userRepository.findByIdOrElseThrow(userId);

        //요청 조회
		FeedbackRequestEntity request = feedbackRequestEntityRepository.findByIdOrElseThrow(feedbackRequestId);
		Long getUserId = request.getUser().getUserId();
		Long getTutorId = request.getTutor().getUserId();

		// 작성 유저/ 작성 대상 튜터인지 확인(상세보기)
		if (user.getRole().equals(UserRole.STUDENT)) {
			if (!user.getUserId().equals(getUserId)) {
				throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
			}
		} else if (user.getRole().equals(UserRole.APPROVED_TUTOR)) {
			if (!user.getUserId().equals(getTutorId)) {
				throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
			}
		} else {
			// STUDENT도 튜터도 아닌 경우 무조건 접근 금지
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		return new FeedbackRequestDetailsDto(request);
	}

	@Transactional(readOnly = true)
	@Override
	public List<TutorFeedbackResponseDetailsDto> getFeedbackRequests(Long tutorId, Integer page, Integer size){
		User tutor = userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR);
		if(!tutor.getRole().equals(UserRole.APPROVED_TUTOR)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<FeedbackRequestEntity> requests = feedbackRequestEntityRepository.getPagedRequestsForTutor(
			tutor.getUserId(),
			RequestState.PENDING,
			pageable
		);
		return requests.stream()
			.map(TutorFeedbackResponseDetailsDto:: new)
			.collect(Collectors.toList());

	}

	@Transactional
	@Override
	public TutorFeedbackResponseDetailsDto acceptRequest(Long tutorId, Long feedbackRequestId) {
		// 1. 튜터 본인 확인
		User tutor = userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR);
		if(!tutor.getRole().equals(UserRole.APPROVED_TUTOR)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}

		FeedbackRequestEntity request = feedbackRequestEntityRepository.findByIdOrElseThrow(feedbackRequestId);

		// 2. 피드백 상태 확인 (Pending)
		if(!request.getRequestState().equals(RequestState.PENDING)){
			throw new CustomException(ErrorCode.CANNOT_REJECT_NON_PENDING_FEEDBACK);
		}

		request.updateRequestState(RequestState.IN_PROGRESS);
		FeedbackRequestEntity getInfoRequest =feedbackRequestEntityRepository.save(request);
		pointService. makeIncome(request);
		Map<String, Object> data =  new LinkedHashMap<>();
		data.put("modifiedAt ", request.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		return new TutorFeedbackResponseDetailsDto(getInfoRequest);
	}



	@Transactional
	@Override
	public TutorFeedbackResponseDetailsDto rejectFeedbackRequest(
		Long tutorId,
		Long feedbackRequestId,
		Integer rejectNumber,
		FeedbackRejectRequestDto dto){

		// 1. 튜터 본인 확인
		User tutor = userRepository.findByIdOrElseThrow(tutorId, ErrorCode.NOT_FOUND_TUTOR);
		if(!tutor.getRole().equals(UserRole.APPROVED_TUTOR)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}

		// 2. 피드백 요청 존재 여부 확인
		FeedbackRequestEntity request = feedbackRequestEntityRepository.findByIdOrElseThrow(feedbackRequestId);

		// 2. 피드백 존재 여부 확인(Pendding 상태 거절)
		if(!request.getRequestState().equals(RequestState.PENDING)){
			throw new CustomException(ErrorCode.CANNOT_REJECT_NON_PENDING_FEEDBACK);
		}
		//3. 본인에게 신청한 피드백인지 검사
		if(!request.getTutor().getUserId().equals(tutor.getUserId())){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		RejectReason rejectReason = RejectReason.fromNumber(rejectNumber);
		request.updateRequestState(RequestState.REJECTED);
		request.updateFeedbackRequestRejectDto(rejectReason, dto.getEtcReason());
		pointService.refundPoint(request);
		return new TutorFeedbackResponseDetailsDto(request);
	}
}
