package com.example.feedprep.domain.feedbackrequestentity.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRejectRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.FeedbackRequestEntityResponseDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.FeedbackResponseAllDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.FeedbackResponseDetailsDto;
import com.example.feedprep.domain.feedbackrequestentity.service.FeedbackRequestService;

@RestController
@RequestMapping("/feedback-requests")
@RequiredArgsConstructor
public class FeedbackRequestController {
    private final FeedbackRequestService feedbackRequestService;

	@PostMapping
	public ResponseEntity<ApiResponseDto<FeedbackRequestEntityResponseDto>> createRequest(
		@AuthUser Long userId,
		@Validated @RequestBody FeedbackRequestDto dto){

		return ResponseEntity.status( HttpStatus.CREATED)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_FEEDBACK_REQUEST_CREATED,
				feedbackRequestService.createRequest(userId, dto)));
	}

	@GetMapping
	public ResponseEntity<ApiResponseDto<List<FeedbackRequestEntityResponseDto>>> getRequest(
		@AuthUser Long userId,
		@RequestParam(required = false) Long tutorId,
		@RequestParam(required = false) Long documentId,
		@RequestParam(required = false) String inputMonth,
		@RequestParam(required = false) Integer stateNumber,
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "20") Integer size
	){
		LocalDateTime month = null;
		if(inputMonth != null){
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
			YearMonth ym = YearMonth.parse(inputMonth, dateTimeFormatter);
			month = ym.atDay(1).atStartOfDay();  // LocalDateTime으로 변환
		}
		RequestState requestState = null;
		Integer num= stateNumber;
		if(num !=null){
			requestState = RequestState.fromNumber(num);
		}
		return ResponseEntity.status( HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_FEEDBACK_REQUEST,
				feedbackRequestService.getRequests(userId,
			tutorId,
			documentId,
			month,
			requestState,
			page,
			size)));
	}

	@PutMapping("/{requestId}")
	public ResponseEntity<ApiResponseDto<FeedbackRequestEntityResponseDto>> updateRequest(
		@AuthUser Long userId,
		@PathVariable Long requestId,
		@Validated @RequestBody FeedbackRequestDto dto
	){
		return ResponseEntity.status( HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_FEEDBACK_REQUEST_UPDATE,
				feedbackRequestService.updateRequest(userId,requestId, dto)));
	}
	@DeleteMapping("/{requestId}")
	public ResponseEntity<ApiResponseDto<FeedbackRequestEntityResponseDto>> cancelRequest(
		@AuthUser Long userId,
		@Validated @PathVariable Long requestId
	){
		return ResponseEntity.status( HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_FEEDBACK_REQUEST_CANCELED,
				feedbackRequestService.cancelRequest(userId,requestId)));
	}
	@GetMapping("{requestId}")
	public ResponseEntity<ApiResponseDto<FeedbackResponseAllDto>>  getFeedbackRequest(
		@AuthUser Long userId,
		@PathVariable  Long requestId
	){
		return ResponseEntity.status( HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_FEEDBACK_REQUEST,
				feedbackRequestService.getFeedbackRequest(userId, requestId)));
	}
	//튜터
	@GetMapping("/tutor")
	public ResponseEntity<ApiResponseDto<List<FeedbackResponseDetailsDto>>> getFeedbackRequests(
		@AuthUser Long tutorId,
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "20") Integer size
	){
		return ResponseEntity.status( HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_FEEDBACK_REQUEST,
			feedbackRequestService.getFeedbackRequests(tutorId, page, size)));
	}

	@PatchMapping("/{requestId}")
	public ResponseEntity<ApiResponseDto<FeedbackRequestEntityResponseDto>> rejectFeedbackRequest(
		@AuthUser Long tutorId,
		@PathVariable Long requestId,
		@RequestParam Integer rejectNumber,
		@Validated @RequestBody FeedbackRejectRequestDto dto){
		return ResponseEntity.status( HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_FEEDBACK_REQUEST_REJECTED,
				feedbackRequestService.rejectFeedbackRequest(tutorId, requestId, rejectNumber, dto)));
	}
}
