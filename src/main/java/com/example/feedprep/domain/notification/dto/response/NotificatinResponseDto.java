package com.example.feedprep.domain.notification.dto.response;


import lombok.Getter;

import com.example.feedprep.domain.notification.entity.Notification;
import com.example.feedprep.domain.notification.enums.NotificationType;

@Getter
public class NotificatinResponseDto {
	private Long id;

	private NotificationType notificationType;

	private String content;

	private String url;

	private boolean isRead;

	public NotificatinResponseDto(Notification notification) {
		this.id = notification.getId();
		this.notificationType = notification.getNotificationType();
		this.content = notification.getContent();
		this.url = notification.getUrl();
		this.isRead = notification.isRead();
	}
}
