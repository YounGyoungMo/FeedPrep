package com.example.feedprep.domain.notification.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;

import com.example.feedprep.common.entity.BaseTimeEntity;
import com.example.feedprep.domain.notification.enums.NotificationType;

@Getter
@Entity
@Table(name = "notifications")
public class Notification extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "type")
	private NotificationType notificationType;

	@Column(name = "sender_id")
	private Long senderId;

	@Column(name = "receiver_id")

	private Long receiverId;

	private String content;

	private String url;

	private boolean isRead;

	private boolean isStale;

	public Notification() {

	}

	public Notification(
		NotificationType notificationType,
		Long senderId,
		Long receiverId,
		String message,
		String urlTemplate) {

		this.notificationType = notificationType;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = message;
		this.url = urlTemplate;
	}

	public void updateReadState(boolean isRead){
		this.isRead = isRead;
	}
	public void updateStaleState(boolean isStale){
		this.isStale = isStale;
	}
}
