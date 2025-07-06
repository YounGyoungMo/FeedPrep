package com.example.feedprep.domain.notification.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.s3.service.S3ServiceImpl;
import com.example.feedprep.domain.notification.dto.response.NotificationGetCountDto;
import com.example.feedprep.domain.notification.dto.response.NotificationResponseDto;
import com.example.feedprep.domain.notification.entity.Notification;
import com.example.feedprep.domain.notification.enums.NotificationType;
import com.example.feedprep.domain.notification.repository.NotificationRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;

	@Qualifier("stringRedisTemplate")
	private final RedisTemplate<String, String> statusTemplate;

	private final RedissonClient redissonClient;

	private final NotificationPushService notificationPushService;

	private static final Logger slackLogger = LoggerFactory.getLogger(S3ServiceImpl.class);

	@Override
	public NotificationResponseDto sendNotification(User sender, User receiver, Integer type) {

		Long senderUserId = sender.getUserId();
		Long receiverUserId = receiver.getUserId();
		NotificationType notificationType =  NotificationType.fromNumber(type);
        String message = notificationType.buildMessage(sender.getName())
			.orElseThrow(()->new RuntimeException("발송 실패"));
		Notification notification =
			new Notification(notificationType
			,senderUserId
			,receiverUserId
			, message
			,notificationType.getUrlTemplate()
		);

		Notification saveNotification = notificationRepository.save(notification);
		notificationPushService.sendToUser(receiverUserId);
		return new NotificationResponseDto(saveNotification);
	}

	@Transactional(readOnly = true)
	@Override
	public List<NotificationResponseDto> getNotifications(Long userId, Integer page, Integer size) {
		User receiverUser = userRepository.findByIdOrElseThrow(userId);
		if(!receiverUser.getUserId().equals(userId)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return notificationRepository
			.findNotificationByReceiverId(receiverUser.getUserId(), pageable)
			.stream()
			.map(NotificationResponseDto:: new )
			.toList();
	}

	@Transactional
	@Override
	public NotificationResponseDto isRead(Long userId, Long notificationId) {
		Notification notification = notificationRepository.findByIdOrElseThrow(notificationId);
		if(!notification.getReceiverId().equals(userId)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		notification.updateReadState(true);
		return new NotificationResponseDto(notification);
	}

	@Transactional
	@Override
	public Map<String , Object> deleteNotification(Long userId, Long notificationId) {
		User user = userRepository.findByIdOrElseThrow(userId);
		Notification notification = notificationRepository.findByIdOrElseThrow(notificationId);
		if(!notification.getReceiverId().equals(user.getUserId())){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		notificationRepository.delete(notification);
		Map<String , Object> data = new HashMap<>();
		data.put("date",  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		return data;

	}

	@Override
	public NotificationGetCountDto getNotificationCount(Long receiverId) {
		Long count =  notificationRepository.getCountByReceiver(receiverId);
		NotificationGetCountDto notificationGetCountDto = new NotificationGetCountDto(count);
		return notificationGetCountDto;
	}

	public List<Notification> getAllNotifications() {
		return notificationRepository.findAll();
	}

	public void cleanupNotifications(List<Notification> list) {
		LocalDateTime now = LocalDateTime.now();
		for (Notification notification : list) {
			Duration TTl = Duration.between(notification.getCreatedAt().truncatedTo(ChronoUnit.DAYS) , now);
			long days = TTl.toDays();
			if (days >= 30) {
				notificationRepository.delete(notification);
			} else if (days >= 7) {
				notification.updateStaleState(true);
			}
		}
	}


	@Transactional
	@Scheduled(cron = "0 0 2 * * *")
	public void scheduledNotificationCleanup() {
		String status = statusTemplate.opsForValue().get("status:notificationCheck");
		if (status != null && status.equals("processing")) {
			//log.info("[updateRatings] 다른 서버에서 캐싱 중이라 패스합니다.");
			return; // 락 시도 없이 종료
		}

		//락
		RLock lock = redissonClient.getLock("lock:notificationCheck");
		boolean isLocked = false;

		try{
			//최대 2초 동안 락 대기, 락 획득 시 3초간 유지( 그 이후 자동 해제 됨)
			isLocked = lock.tryLock(2,3, TimeUnit.SECONDS);

			if(isLocked) {
				statusTemplate.opsForValue().set("status:notificationCheck", "processing", 10, TimeUnit.MINUTES);
				List<Notification> getNotifications = getAllNotifications();
				if (!getNotifications.isEmpty()) {
					cleanupNotifications(getNotifications);
				}
				statusTemplate.opsForValue().set("status:notificationCheck", "done", 10, TimeUnit.MINUTES);
				slackLogger.info("상태 완료: status:notificationCheck = done 알림 정리 완료: 총 {}건 처리됨", getNotifications.size());
			}
		} catch(InterruptedException ex){
			slackLogger.warn("[업데이트 실패] 락 대기 중 인터럽트 발생", ex);
			Thread.currentThread().interrupt(); // 복원
		}catch (Exception ex) {
			statusTemplate.opsForValue().set("status:notificationCheck", "error", 10, TimeUnit.MINUTES);
			slackLogger.error("[알림 정리 시스템] 예외 발생", ex);
		}
		finally {
			if(isLocked){
				lock.unlock();
			}
		}
	}
}
