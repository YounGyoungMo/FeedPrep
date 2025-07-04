package com.example.feedprep.domain.notification.contoller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.domain.notification.dto.response.NotificationGetCountDto;
import com.example.feedprep.domain.notification.dto.response.NotificationResponseDto;
import com.example.feedprep.domain.notification.service.NotificationPushService;
import com.example.feedprep.domain.notification.service.NotificationServiceImpl;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationServiceImpl notificationService;
    private final NotificationPushService notificationPushService;

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@RequestParam String token){
		return notificationPushService.validateTokenAndCreateEmitter(token);
	}

	@GetMapping("/count")
	public ResponseEntity<ApiResponseDto<NotificationGetCountDto>>getCount(
		@AuthUser Long userId
	){
		return  ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseDto.success(
				SuccessCode.OK_SUCCESS_NOTIFICATION,
				notificationService.getNotificationCount(userId)));
	}

	@GetMapping
	public ResponseEntity<ApiResponseDto<List<NotificationResponseDto>>> getNotificationList(
		@AuthUser Long userId,
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "10") Integer size
	){
		return  ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseDto.success(
				SuccessCode.OK_SUCCESS_NOTIFICATION,
				notificationService.getNotifications(userId, page, size)));
	}

	@PatchMapping("/{notificationId}/Read")
	public ResponseEntity<ApiResponseDto<NotificationResponseDto>> isReadNotification(
		@AuthUser Long userId,
		@PathVariable Long notificationId){
		return  ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_NOTIFICATION_IS_READ,
				notificationService.isRead(userId,notificationId)));
	}

	@DeleteMapping("/{notificationId}")
	public ResponseEntity<ApiResponseDto<Map<String, Object>>> deleteNotification(@AuthUser Long userId, @PathVariable Long notificationId){
		return  ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_FEEDBACK_REVIEW_DELETED,
				notificationService.deleteNotification(userId, notificationId)));
	}
}
