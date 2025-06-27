package com.example.feedprep.common.sse.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {
	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public void save(Long userId, SseEmitter emitter){
		emitters.put(userId, emitter);
	}

	public Optional<SseEmitter> get(Long userId){
		return Optional.ofNullable(emitters.get(userId));
	}

	public void delete(Long userId){
		emitters.remove(userId);
	}
}
