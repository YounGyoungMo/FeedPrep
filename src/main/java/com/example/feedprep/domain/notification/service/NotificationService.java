package com.example.feedprep.domain.notification.service;

import java.util.List;

import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.notification.dto.response.NotificationResponseDto;

public interface NotificationService {

	 NotificationResponseDto sendNotification(Long senderId, Long receiverId, Integer type);

	 NotificationResponseDto sendAdminNotification(Long adminId, Long receiverId, Integer type, String message);

	 List<NotificationResponseDto> getNotifications(Long userId );

	 NotificationResponseDto isRead(Long userId, Long NotificationId);

	 ApiResponseDto deleteNotification(Long userId, Long notificationId );

	Long getNotificationCount(Long receiverId);
}
