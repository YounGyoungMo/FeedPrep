package com.example.feedprep.domain.notification.service;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.feedprep.common.sse.repository.EmitterRepository;

@Service
@RequiredArgsConstructor
public class NotificationPushService {

	private final EmitterRepository emitterRepository;

	public void sendToUser(Long userId, Object data){
		emitterRepository.get(userId).ifPresent(emitter -> {
			try{
				emitter.send(SseEmitter.event()
					.name("unread-count")
					.data(data));
			}catch (IOException e){
				emitter.completeWithError(e);
				emitterRepository.delete(userId);
			}
		});
	}
}
