package com.example.feedprep.domain.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import com.example.feedprep.domain.notification.entity.Notification;
import com.example.feedprep.domain.notification.repository.NotificationRepository;
import com.example.feedprep.domain.notification.service.NotificationServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
@Sql(scripts = {
	"classpath:/db/init_table.sql",
	"classpath:/db/data_init.sql"
})
public class NotificationSpringbootTest {
	@Autowired
	@Qualifier("stringRedisTemplate")
	RedisTemplate<String, String> statusTemplate;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	NotificationServiceImpl notificationService;

	@BeforeEach
	void clearRedis() {
		statusTemplate.delete("status:notificationCheck");
	}

	@Test
	void 알림_정리_정상_작동_삭제_및_갱신(){

		//given
		notificationService.scheduledNotificationCleanup();

		//when
		List<Notification> remainingCheck = notificationRepository.findAll();
		for(Notification no : remainingCheck) {
			System.out.println(no.getCreatedAt());
		}

		//then
		// 1개는 삭제됨 (30일 초과)
		// 1개는 stale 처리됨
		// 1개는 그대로 남음
		assertEquals(2, remainingCheck.size());

		long staleCount = remainingCheck.stream().filter(Notification::isStale).count();
		assertEquals(1, staleCount);
	}

	@Test
	void 캐시_상태_검증(){
		String value = statusTemplate.opsForValue().get("status:notificationCheck");
		assertEquals("done", value);
	}
}
