// package com.example.feedprep.domain.notification;
//
// import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
// import java.time.LocalDateTime;
// import java.util.List;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.jdbc.Sql;
// import org.springframework.transaction.annotation.Transactional;
// import com.example.feedprep.domain.notification.entity.Notification;
// import com.example.feedprep.domain.notification.repository.NotificationRepository;
// import com.example.feedprep.domain.notification.service.NotificationServiceImpl;
//
// @Slf4j
// @SpringBootTest
// @ActiveProfiles("test")
// @RequiredArgsConstructor
// @Sql(scripts = {
// 	"classpath:/db/init_table.sql",
// 	"classpath:/db/data_init.sql"
// })
// //테스트 마다 DB 깨끗하게 초기화
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// public class NotificationSpringbootTest {
// 	@Autowired
// 	@Qualifier("stringRedisTemplate")
// 	RedisTemplate<String, String> statusTemplate;
//
// 	@Autowired
// 	NotificationRepository notificationRepository;
//
// 	@Autowired
// 	NotificationServiceImpl notificationService;
//
// 	@Transactional
// 	@Test
// 	void 알림_정리_정상_작동_삭제_및_갱신(){
//
// 		//given
// 		notificationService.scheduledNotificationCleanup();
//
// 		//when
// 		List<Notification> remainingCheck = notificationRepository.findAll();
// 		for(Notification no : remainingCheck) {
// 			System.out.println(no.getCreatedAt());
// 		}
//
// 		//then
// 		// 1개는 삭제됨 (30일 초과)
// 		// 1개는 stale 처리됨
// 		// 1개는 그대로 남음
// 		assertEquals(2, remainingCheck.size());
//
// 		long staleCount = remainingCheck.stream().filter(Notification::isStale).count();
// 		assertEquals(1, staleCount);
// 	}
//
// 	@Transactional
// 	@Test
// 	void 알림_30일_넘으면_삭제() {
// 		List<Notification> before = notificationRepository.findAll();
// 		// 30일 초과 알림 포함된 가짜 리스트
// 		LocalDateTime base = LocalDateTime.now();
// 		LocalDateTime limit = base.minusDays(30);
// 		List<Notification> oldNotifications = notificationRepository.findAllOlderThan(limit);
//
// 		notificationService.cleanupNotifications( oldNotifications);
//
// 		List<Notification> after = notificationRepository.findAll();
// 		System.out.println("삭제 전 수: " + before.size());
// 		System.out.println("삭제 대상 수: " + oldNotifications.size());
// 		System.out.println("삭제 후 수: " + after.size());
// 		// then 검증
// 		assertNotNull(after);
// 		assertEquals(2,  after.size());
//
// 	}
// 	@Transactional
// 	@Test
// 	void 알림_10일_넘으면_삭제() {
// 		List<Notification> before = notificationRepository.findAll();
// 		// 11일 이전 데이터 중에 7일 미만 제외하고 숨김
// 		LocalDateTime limit = LocalDateTime.now().minusDays(7);
// 		List<Notification> oldNotifications = notificationRepository.findAllOlderThan(limit);
//
// 		notificationService.cleanupNotifications( oldNotifications);
// 		List<Notification> after = notificationRepository.findAll();
//
// 		long staleCount = after.stream().filter(Notification::isStale).count();
// 		assertNotNull(after);
// 		System.out.println("삭제 전 수: " + before.size());
// 		System.out.println("삭제 대상 수: " + oldNotifications.size());
// 		System.out.println("삭제 후 수: " + after.size());
// 		assertEquals(1, staleCount);
//
// 		// then 검증
// 	}
//
// 	@Transactional
// 	void 알림_7일_넘으면_삭제() {
// 		List<Notification> before = notificationRepository.findAll();
// 		// 11일 이전 데이터 중에 7일 미만 제외하고 숨김
// 		LocalDateTime limit = LocalDateTime.now().minusDays(8);
// 		List<Notification> oldNotifications = notificationRepository.findAllOlderThan(limit);
//
// 		notificationService.cleanupNotifications( oldNotifications);
// 		List<Notification> after = notificationRepository.findAll();
// 		assertNotNull(after);
// 		System.out.println("삭제 전 수: " + before.size());
// 		System.out.println("삭제 대상 수: " + oldNotifications.size());
// 		System.out.println("삭제 후 수: " + after.size());
// 		assertEquals(2, after.size());
// 	}
// }
