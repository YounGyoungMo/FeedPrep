package com.example.feedprep.domain.notification.contoller;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.common.sse.repository.EmitterRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

	private final EmitterRepository emitterRepository;


	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@AuthUser Long userId){
		SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 타임 아웃

		emitterRepository.save(userId, emitter);

		// ③ emitter 종료 감지 등록 (이건 '콜백'이지 실행이 아님)
		emitter.onCompletion(()-> emitterRepository.delete(userId));
		emitter.onTimeout(() -> emitterRepository.delete(userId));
		emitter.onError((e)->emitterRepository.delete(userId));

		// ④ 여기서 클라이언트로 응답 = SSE 연결 시작
		return emitter;
	}
}
