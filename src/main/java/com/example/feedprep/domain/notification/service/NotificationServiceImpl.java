package com.example.feedprep.domain.notification.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.notification.dto.response.NotificationResponseDto;
import com.example.feedprep.domain.notification.entity.Notification;
import com.example.feedprep.domain.notification.enums.NotificationType;
import com.example.feedprep.domain.notification.repository.NotificationRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;

	@Override
	public NotificationResponseDto sendNotification(Long senderId, Long receiverId, Integer type) {

		User senderUser = userRepository.findByIdOrElseThrow(senderId);
		User receiverUser = userRepository.findByIdOrElseThrow(receiverId);
		NotificationType notificationType =  NotificationType.fromNumber(type);

		Notification notification =
			new Notification(notificationType
			,senderUser.getUserId()
			, receiverUser.getUserId()
			, notificationType.buildMessage()
			,notificationType.getUrlTemplate()
		);

		Notification saveNotification = notificationRepository.save(notification);
		return new NotificationResponseDto(saveNotification);
	}

	@Override
	public NotificationResponseDto sendAdminNotification(Long adminId, Long receiverId, Integer type, String message) {
		User senderUser = userRepository.findByIdOrElseThrow(adminId);
		User receiverUser = userRepository.findByIdOrElseThrow(receiverId);
		NotificationType notificationType =  NotificationType.fromNumber(type);
		Notification notification =
			new Notification(notificationType
				,senderUser.getUserId()
				, receiverUser.getUserId()
				, notificationType.buildMessage()
				,notificationType.getUrlTemplate()
			);

		Notification saveNotification = notificationRepository.save(notification);
		return new NotificationResponseDto(saveNotification);
	}

	@Transactional(readOnly = true)
	@Override
	public List<NotificationResponseDto> getNotifications(Long userId) {
		User receiverUser = userRepository.findByIdOrElseThrow(userId);
		if(receiverUser.getUserId().equals(userId)){
			throw  new RuntimeException("접근 불가");
		}
		List<Notification> notifications = notificationRepository
			.findNotificationByReceiverId(receiverUser.getUserId())
			.stream()
			.toList();

		return notifications.stream().map(NotificationResponseDto:: new ).toList();
	}

	@Transactional
	@Override
	public NotificationResponseDto isRead(Long userId, Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(()-> new RuntimeException("없음"));
		if(notification.getReceiverId().equals(userId)){
			throw new RuntimeException("");
		}
		notification.updateReadState(true);
		return new NotificationResponseDto(notification);
	}

	@Transactional
	@Override
	public ApiResponseDto deleteNotification(Long userId, Long notificationId) {
		User user = userRepository.findByIdOrElseThrow(userId);
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(()-> new RuntimeException("없음"));
		if(!notification.getReceiverId().equals(user.getUserId())){
			throw new RuntimeException("동일 인물 아님 삭제 불가");
		}
		notificationRepository.delete(notification);

		return new ApiResponseDto<>(SuccessCode.OK_SUCCESS_Notification_DELETED.getHttpStatus().value(),
			                        SuccessCode.OK_SUCCESS_Notification_DELETED.getMessage(),
			                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

	}

	@Override
	public Long getNotificationCount(Long receiverId) {
		return notificationRepository.getCount();
	}
}
