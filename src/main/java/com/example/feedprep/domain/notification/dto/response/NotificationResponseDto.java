package com.example.feedprep.domain.notification.dto.response;


import java.time.LocalDateTime;

import lombok.Getter;

import com.example.feedprep.domain.notification.entity.Notification;
import com.example.feedprep.domain.notification.enums.NotificationType;

@Getter
public class NotificationResponseDto {
	private Long id;
	private NotificationType notificationType;
	private String content;
	private String url;
	private boolean isRead;
	private LocalDateTime createdAt;

	public NotificationResponseDto(Notification notification) {
		this.id = notification.getId();
		this.notificationType = notification.getNotificationType();
		this.content = notification.getContent();
		this.url = notification.getUrl();
		this.isRead = notification.isRead();
		this.createdAt = notification.getCreatedAt();
	}
}
