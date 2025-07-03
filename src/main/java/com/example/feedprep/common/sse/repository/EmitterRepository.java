package com.example.feedprep.common.sse.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {
	private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

	public void save(Long userId, SseEmitter emitter){
		emitters.computeIfAbsent(userId, k->new CopyOnWriteArrayList<>()).add(emitter);
	}

	public List<SseEmitter> get(Long userId){
		return  emitters.getOrDefault(userId, Collections.emptyList());
	}

	public void delete(Long userId, SseEmitter emitter)

	{
		List<SseEmitter> emitterList = emitters.get(userId);
		if(emitterList != null ){
			emitterList.remove(emitter);
			if(!emitterList.isEmpty()){
				emitters.remove(userId);
			}
		}
	}
}
