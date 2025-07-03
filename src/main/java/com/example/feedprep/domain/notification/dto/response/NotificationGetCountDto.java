package com.example.feedprep.domain.notification.dto.response;

import lombok.Getter;

@Getter
public class NotificationGetCountDto {

	private Long count;
	public NotificationGetCountDto(Long count) {
		this.count = count;
	}
}
