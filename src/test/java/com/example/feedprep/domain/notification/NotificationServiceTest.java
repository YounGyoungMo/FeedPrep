package com.example.feedprep.domain.notification;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.notification.dto.response.NotificationGetCountDto;
import com.example.feedprep.domain.notification.dto.response.NotificationResponseDto;
import com.example.feedprep.domain.notification.enums.NotificationType;
import com.example.feedprep.domain.notification.repository.NotificationRepository;
import com.example.feedprep.domain.notification.service.NotificationPushService;
import com.example.feedprep.domain.notification.service.NotificationServiceImpl;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.example.feedprep.domain.notification.entity.Notification;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

	@Mock
	private NotificationRepository notificationRepository;

    @Mock
	private UserRepository userRepository;

	@Mock
	private RedissonClient redissonClient;

	@Mock
	private RedisTemplate<String, String> statusTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private NotificationPushService notificationPushService;
	@InjectMocks
	private NotificationServiceImpl notificationService;



	@Test
	void sendNotification_성공적으로_알림_전송() {
		// given
		Long senderId = 1L;
		Long receiverId = 2L;
		Integer type = 101;

		User sender =mock(User.class);
		when(sender.getUserId()).thenReturn(senderId);
		when(sender.getName()).thenReturn("sender");

		User receiver = mock(User.class);
		when(sender.getUserId()).thenReturn(receiverId);
		when(sender.getName()).thenReturn( "receiver");
		NotificationType notificationType = NotificationType.fromNumber(type);
		String expectedMessage = notificationType.buildMessage(sender.getName()).orElseThrow();

		Notification notification = new Notification(notificationType, senderId, receiverId, expectedMessage, notificationType.getUrlTemplate());

		when(userRepository.findByIdOrElseThrow(senderId)).thenReturn(sender);
		when(userRepository.findByIdOrElseThrow(receiverId)).thenReturn(receiver);
		when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

		// when
		NotificationResponseDto responseDto = notificationService.sendNotification(senderId, receiverId, type);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getNotificationType()).isEqualTo(notificationType);
		assertThat(responseDto.getContent()).isEqualTo(expectedMessage);
		assertThat(responseDto.getReceiverId()).isEqualTo(receiverId);

		verify(userRepository).findByIdOrElseThrow(senderId);
		verify(userRepository).findByIdOrElseThrow(receiverId);
		verify(notificationRepository).save(any(Notification.class));
		verify(notificationPushService).sendToUser(receiverId);
	}


	@Test
	void getNotification() {

		// given

		Long receiverId = 1L;
		User receiver = mock(User.class);
		when(receiver.getUserId()).thenReturn(receiverId);
		when(userRepository.findByIdOrElseThrow(receiverId)).thenReturn(receiver);


		Notification notification = mock(Notification.class);
		when(notification.getId()).thenReturn(1L);
		when(notification.getContent()).thenReturn("%s 님께 피드백을 요청했습니다.");
        when(notification.getReceiverId()).thenReturn(1L);

		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Notification> mockPage = new PageImpl<>(List.of(notification), pageRequest, 1);

		when(notificationRepository.findNotificationByReceiverId(receiverId, pageRequest))
			.thenReturn(mockPage);

		List<NotificationResponseDto> responseDtos = notificationService.getNotifications(receiverId, 0 ,10);

		assertEquals(1,  responseDtos.size());
		assertEquals(1L, responseDtos.get(0).getId());
		assertEquals(1L, responseDtos.get(0).getReceiverId());
		assertEquals("%s 님께 피드백을 요청했습니다.", responseDtos.get(0).getContent());

	}

	@Test
	void isReadNotification() {

		// given

		Long receiverId = 1L;
		User receiver = mock(User.class);

		Notification notification = mock(Notification.class);
		when(notification.getId()).thenReturn(1L);
		when(notification.getContent()).thenReturn("%s 님께 피드백을 요청했습니다.");
		when(notification.getReceiverId()).thenReturn(receiverId);
		when(notification.getUrl()).thenReturn("null");
		when(notification.isRead()).thenReturn(true);

		when(notificationRepository.findByIdOrElseThrow(1L)).thenReturn(notification);

		NotificationResponseDto responseDto =
			notificationService.isRead(receiverId, 1L);

		assertEquals(1L, responseDto.getId());
		assertEquals(1L, responseDto.getReceiverId());
		verify(notification, times(1)).updateReadState(true);
	}

	@Disabled
	@Test
	void deleteNotification() {
		// given
		Long receiverId = 1L;
		User receiver = mock(User.class);
		when(receiver.getUserId()).thenReturn(receiverId);
		when(userRepository.findByIdOrElseThrow(receiverId)).thenReturn(receiver);

		Notification notification = mock(Notification.class);
		when(notification.getReceiverId()).thenReturn(receiverId);

		when(notificationRepository.findByIdOrElseThrow(1L)).thenReturn(notification);

		ApiResponseDto responseDto =
			// when
			notificationService.deleteNotification(receiverId, 1L);

		verify(notificationRepository, times(1)).delete(notification);
	}

	@Test
	void getNotificationCount(){
		// given
		Long receiverId = 1L;
		when(notificationRepository.getCountByReceiver(receiverId)).thenReturn(1L);

		// when
		NotificationGetCountDto count = notificationService.getNotificationCount(receiverId);

		// then
		assertEquals(1L, count.getCount());
	}



	@Test
	public void Scheduler_락_점유_실패_시_동작() throws InterruptedException {
		// given
		List<Notification> mockList = List.of(mock(Notification.class));

		// 락 획득용 mocking
		RLock lock = mock(RLock.class);
		when(redissonClient.getLock(anyString())).thenReturn(lock);
		when(statusTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("status:notificationCheck")).thenReturn(null);

		// when
		notificationService.scheduledNotificationCleanup();

		// then
		verify(notificationRepository, never()).findByCreatedAtBeforeOrderByCreatedAtAsc(any());
	}

	@Test
	public void Redis_상태캐시_키가_존재하는_경우() throws InterruptedException {
		// given
		List<Notification> mockList = List.of(mock(Notification.class));

		// 락 획득용 mocking
		RLock lock = mock(RLock.class);
		when(redissonClient.getLock(anyString())).thenReturn(lock);
		when(statusTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("status:notificationCheck")).thenReturn("LOCKED");

		// when
		notificationService.scheduledNotificationCleanup();

		// then
		verify(notificationRepository, never()).findByCreatedAtBeforeOrderByCreatedAtAsc(any());
	}
	@Test
	void 존재하지_않는_사용자() {
		// given
		Long senderId = 99L;
		Long receiverId = 1L;

		when(userRepository.findByIdOrElseThrow(senderId))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		// when
		CustomException exception = assertThrows(CustomException.class, () -> {
			notificationService.sendNotification(senderId, receiverId, 101);
		});

		// then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

	}

	@Test
	void 존재하지_않는_알림_ID로_조회() {
		// given
		Long receiverId = 1L;
		Long nonExistentNotificationId = 999L;

		when(notificationRepository.findByIdOrElseThrow(nonExistentNotificationId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION));

		// when
		CustomException exception = assertThrows(CustomException.class, () -> {
			notificationService.isRead(receiverId, nonExistentNotificationId);
		});

		// then
		assertEquals(ErrorCode.NOT_FOUND_NOTIFICATION, exception.getErrorCode());
	}
	@Test
	void 존재하지_않는_알림_ID로_삭제() {
		// given
		Long receiverId = 1L;
		Long nonExistentNotificationId = 999L;

		when(notificationRepository.findByIdOrElseThrow(nonExistentNotificationId))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION));

		// when
		CustomException exception = assertThrows(CustomException.class, () -> {
			notificationService.deleteNotification(receiverId, nonExistentNotificationId);
		});

		// then
		assertEquals(ErrorCode.NOT_FOUND_NOTIFICATION, exception.getErrorCode());
	}

}
