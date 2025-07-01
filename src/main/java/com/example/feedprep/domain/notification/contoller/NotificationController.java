package com.example.feedprep.domain.notification.contoller;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;

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
import com.example.feedprep.common.sse.repository.EmitterRepository;
import com.example.feedprep.domain.notification.dto.response.NotificationResponseDto;
import com.example.feedprep.domain.notification.repository.NotificationRepository;
import com.example.feedprep.domain.notification.service.NotificationServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

	private final EmitterRepository emitterRepository;
	private final NotificationServiceImpl notificationService;
	private final NotificationRepository notificationRepository;

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@AuthUser Long userId){
		SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 타임 아웃

		emitterRepository.save(userId, emitter);
		long unreadCount = notificationRepository.getCountByReceiver(userId);
		try{

			emitter.send(SseEmitter.event()
				.name("unread-count")
				.data(unreadCount));
		}catch (IOException e){
			emitter.completeWithError(e);
			emitterRepository.delete(userId);
		}

		// ③ emitter 종료 감지 등록 (이건 '콜백'이지 실행이 아님)
		emitter.onCompletion(()-> emitterRepository.delete(userId));
		emitter.onTimeout(() -> emitterRepository.delete(userId));
		emitter.onError((e)->emitterRepository.delete(userId));

		// ④ 여기서 클라이언트로 응답 = SSE 연결 시작
		return emitter;
	}
	@GetMapping
	public ResponseEntity<ApiResponseDto<List<NotificationResponseDto>>> getNotificationList(
		@AuthUser Long userId,
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "10") Integer size
	){
		return  ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseDto.success(
				SuccessCode.OK_SUCCESS_Notification,
				notificationService.getNotifications(userId, page, size)));
	}

	@PatchMapping("/{notificationId}")
	public ResponseEntity<ApiResponseDto<NotificationResponseDto>> isReadNotification(
		@AuthUser Long userId,
		@PathVariable Long notificationId){
		return  ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseDto.success(SuccessCode.OK_SUCCESS_Notification_IS_READ,
				notificationService.isRead(userId,notificationId)));
	}

	@DeleteMapping("/{notificationId}")
	public ResponseEntity<ApiResponseDto> deleteNotification(@AuthUser Long userId, @PathVariable Long notificationId){
		return new ResponseEntity<>(notificationService.deleteNotification(userId, notificationId), HttpStatus.OK);
	}
}
