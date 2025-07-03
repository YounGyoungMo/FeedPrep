package com.example.feedprep.domain.notification.service;

import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.feedprep.common.security.jwt.JwtUtil;
import com.example.feedprep.common.sse.repository.EmitterRepository;
import com.example.feedprep.domain.notification.repository.NotificationRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPushService {

	private final EmitterRepository emitterRepository;
	private final NotificationRepository notificationRepository;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public SseEmitter validateTokenAndCreateEmitter(String token) {
		Claims claims = jwtUtil.validateToken(token);
		String username = claims.getSubject();

		User user= userRepository.getUserByEmailOrElseThrow(username);
		Long userId = user.getUserId();

		SseEmitter emitter = new SseEmitter(60 * 1000L); // 초타임 아웃
		emitterRepository.save(userId, emitter);
		log.info("userId: {}", userId);

		emitter.onCompletion(() -> emitterRepository.delete(userId, emitter));
		emitter.onTimeout(() -> emitterRepository.delete(userId, emitter));
		emitter.onError((e) -> emitterRepository.delete(userId, emitter));
		sendToUser(userId);
		return emitter;
	}


	public void sendToUser(Long userId){
		List<SseEmitter> emitters = emitterRepository.get(userId);
		if(emitters != null){
			for(SseEmitter emitter : emitters){
				try{

					long unreadCount = notificationRepository.getCountByReceiver(userId);
					emitter.send(SseEmitter.event()
						.name("count")
						.data(unreadCount));

				}catch (IOException e){
					log.error("SSE 전송 실패", e);
					emitter.completeWithError(e);
					emitterRepository.delete(userId, emitter);
				}

			}
		}
	}

}
