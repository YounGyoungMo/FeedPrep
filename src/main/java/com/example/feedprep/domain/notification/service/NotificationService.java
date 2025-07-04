package com.example.feedprep.domain.notification.service;

import java.util.List;

import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.notification.dto.response.NotificationGetCountDto;
import com.example.feedprep.domain.notification.dto.response.NotificationResponseDto;
import com.example.feedprep.domain.user.entity.User;

public interface NotificationService {

	 NotificationResponseDto sendNotification(User sender, User receiver, Integer type);

	List<NotificationResponseDto> getNotifications(Long userId, Integer page, Integer size);

	 NotificationResponseDto isRead(Long userId, Long notificationId);

	 ApiResponseDto deleteNotification(Long userId, Long notificationId );

	NotificationGetCountDto getNotificationCount(Long receiverId);
}
