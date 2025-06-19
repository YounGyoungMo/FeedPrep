package com.example.feedprep.domain.notification.service;

import java.util.List;

import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.notification.dto.response.NotificatinResponseDto;

public interface NotificationService {

	 NotificatinResponseDto sendNotification(Long senderId, Long receiverId, Integer type);

	 NotificatinResponseDto sendAdminNotification(Long adminId, Long receiverId, Integer type, String message);

	 List<NotificatinResponseDto> getNotifications(Long userId );

	 NotificatinResponseDto isRead(Long userId, Long NotificationId);

	 ApiResponseDto deleteNotification(Long userId, Long notificationId );

	 Integer getNotificationCount(Long receiverId);
}
