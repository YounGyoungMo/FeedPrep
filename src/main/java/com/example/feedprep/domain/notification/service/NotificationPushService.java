// package com.example.feedprep.domain.notification.service;
//
// import java.io.IOException;
//
// import lombok.RequiredArgsConstructor;
//
// import org.springframework.stereotype.Service;
// import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
// import com.example.feedprep.common.sse.repository.EmitterRepository;
// import com.example.feedprep.domain.notification.repository.NotificationRepository;
//
// @Service
// @RequiredArgsConstructor
// public class NotificationPushService {
//
// 	private final EmitterRepository emitterRepository;
// 	private final NotificationRepository notificationRepository;
//
// 	public void sendToUser(Long userId){
// 		emitterRepository.get(userId).ifPresent(emitter -> {
// 			try{
// 		    	long unreadCount = notificationRepository.getCountByReceiver(userId);
// 				emitter.send(SseEmitter.event()
// 					.name("unread-count")
// 					.data(unreadCount));
// 			}catch (IOException e){
// 				System.out.println(e.toString());
// 				emitter.completeWithError(e);
// 				emitterRepository.delete(userId);
// 			}
// 		});
// 	}
// }
