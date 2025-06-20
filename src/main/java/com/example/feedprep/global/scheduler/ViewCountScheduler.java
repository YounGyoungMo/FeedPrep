package com.example.feedprep.global.scheduler;

import com.example.feedprep.domain.board.entity.Board;
import com.example.feedprep.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final BoardRepository boardRepository;

    private static final String VIEW_COUNT_PREFIX = "post:viewcount:";

    @Scheduled(fixedDelay = 300000) // ✅ 5분마다 실행
    public void syncViewCountToDatabase() {
        log.info("▶ Redis 조회수를 DB에 반영합니다.");

        Set<String> keys = redisTemplate.keys(VIEW_COUNT_PREFIX + "*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            try {
                String boardIdStr = key.replace(VIEW_COUNT_PREFIX, "");
                Long boardId = Long.parseLong(boardIdStr);

                String viewCountStr = redisTemplate.opsForValue().get(key);
                if (viewCountStr == null) continue;

                long redisCount = Long.parseLong(viewCountStr);

                // DB에서 게시글 가져와 조회수 반영
                boardRepository.findById(boardId).ifPresent(board -> {
                    board.setViewCount(redisCount); // Entity에 setter 있어야 함
                    boardRepository.save(board); // DB 반영
                    log.info("✔ [{}]번 게시글 조회수 {} 저장 완료", boardId, redisCount);
                });

                redisTemplate.delete(key); // Redis 초기화

            } catch (Exception e) {
                log.error("조회수 반영 실패: key={}, error={}", key, e.getMessage());
            }
        }
    }
}