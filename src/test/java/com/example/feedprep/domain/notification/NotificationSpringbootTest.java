package com.example.feedprep.domain.notification;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;
import com.example.feedprep.domain.notification.entity.Notification;
import com.example.feedprep.domain.notification.enums.NotificationType;
import com.example.feedprep.domain.notification.repository.NotificationRepository;
import com.example.feedprep.domain.notification.service.NotificationServiceImpl;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@RequiredArgsConstructor
public class NotificationSpringbootTest {

	@Autowired
	NotificationRepository notificationRepository;
	@Autowired
	StringRedisTemplate redisTemplate;
	@Autowired
	NotificationServiceImpl notificationService;

	// 시작 전에 DB spring.jpa.hibernate.ddl-auto= "update"하고 진행 할것.
	//실행 후 DB에서 직접 날짜 수정 것.
	@Test
	void setup() {
		LocalDateTime now = LocalDateTime.now();

		// 삭제 대상 (30일 초과)
		Notification oldNotification = new Notification(
			NotificationType.fromNumber(101),
			2L,
			1L,
			"삭제 대상 알림",
			"/dummy-url"
		);
		//ReflectionTestUtils.setField(oldNotification, "createdAt", now.minusDays(31));
		notificationRepository.save(oldNotification);

		// stale 처리 대상 (7~29일)
		Notification staleNotification = new Notification(
			NotificationType.fromNumber(101),
			2L,
			1L,
			"stale 대상 알림",
			"/dummy-url"
		);
		//ReflectionTestUtils.setField(staleNotification, "createdAt", now.minusDays(10));
		notificationRepository.save(staleNotification);

		// 무시 대상 (7일 미만)
		Notification recentNotification = new Notification(
			NotificationType.fromNumber(101),
			2L,
			1L,
			"최근 알림",
			"/dummy-url"
		);
		//ReflectionTestUtils.setField(recentNotification, "createdAt", now.minusDays(3));
		notificationRepository.save(recentNotification);
	}

	@Test
	void 알림_정리_정상_작동_삭제_및_갱신(){

		notificationService.scheduledNotificationCleanup();

		List<Notification> remaining = notificationRepository.findAll();
		for(Notification no : remaining) {
			System.out.println(no.getCreatedAt());
		}
		// 1개는 삭제됨 (30일 초과)
		// 1개는 stale 처리됨
		// 1개는 그대로 남음
		assertEquals(2, remaining.size());

		long staleCount = remaining.stream().filter(Notification::isStale).count();
		assertEquals(1, staleCount);
	}

	@Test
	void 캐시_상태_검증(){
		String value = redisTemplate.opsForValue().get("status:notificationCheck");
		assertEquals("done", value);
	}
}
