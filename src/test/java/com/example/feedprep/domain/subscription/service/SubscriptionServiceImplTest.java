package com.example.feedprep.domain.subscription.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.subscription.dto.SubscriptionResponseDto;
import com.example.feedprep.domain.subscription.entity.Subscription;
import com.example.feedprep.domain.subscription.repository.SubscriptionRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

	@Mock
	private SubscriptionRepository subscriptionRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private SubscriptionServiceImpl subscriptionService;

	@Test
	void subscribe_성공() {
		Long senderId = 1L;
		Long receiverId = 2L;
		User sender = mock(User.class);
		User receiver = mock(User.class);

		when(userRepository.findByIdOrElseThrow(senderId)).thenReturn(sender);
		when(userRepository.findByIdOrElseThrow(receiverId)).thenReturn(receiver);
		when(receiver.getRole()).thenReturn(UserRole.APPROVED_TUTOR);
		when(sender.getRole()).thenReturn(UserRole.STUDENT);

		assertDoesNotThrow(() -> subscriptionService.subscribe(senderId, receiverId));
		verify(subscriptionRepository, times(1)).save(any());
	}

	@Test
	void subscribe_실패_자신이_자신을_구독_요청() {
		Long senderId = 1L;
		Long receiverId = 1L;

		CustomException exception = assertThrows(CustomException.class, () -> {
			subscriptionService.subscribe(senderId, receiverId);
		});

		assertEquals(ErrorCode.CANNOT_SUBSCRIBE_SELF, exception.getErrorCode());
	}

	@Test
	void unsubscribe_성공() {
		Long senderId = 1L;
		Long subscriptionId = 1L;

		User sender = mock(User.class);
		Subscription subscription = mock(Subscription.class);

		when(subscriptionRepository.findByIdOrElseThrow(subscriptionId)).thenReturn(subscription);
		when(subscription.getSender()).thenReturn(sender);
		when(sender.getUserId()).thenReturn(senderId);

		assertDoesNotThrow(() -> subscriptionService.unsubscribe(senderId, subscriptionId));
		verify(subscriptionRepository, times(1)).delete(any());
	}

	@Test
	void unsubscribe_실패_구독_정보가_없음() {
		Long senderId = 1L;
		Long subscriptionId = 1L;

		CustomException customException = new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND);

		when(subscriptionRepository.findByIdOrElseThrow(subscriptionId)).thenThrow(customException);

		CustomException exception = assertThrows(CustomException.class, () -> {
			subscriptionService.unsubscribe(senderId, subscriptionId);
		});

		assertEquals(ErrorCode.SUBSCRIPTION_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void unsubscribe_실패_구독의_주인이_아님() {
		Long senderId = 1L;
		Long anotherId = 2L;
		Long subscriptionId = 1L;

		User sender = mock(User.class);
		Subscription subscription = mock(Subscription.class);

		when(subscriptionRepository.findByIdOrElseThrow(subscriptionId)).thenReturn(subscription);
		when(subscription.getSender()).thenReturn(sender);
		when(sender.getUserId()).thenReturn(anotherId);

		CustomException exception = assertThrows(CustomException.class, () -> {
			subscriptionService.unsubscribe(senderId, subscriptionId);
		});

		assertEquals(ErrorCode.UNAUTHORIZED_SUBSCRIPTION_ACCESS, exception.getErrorCode());
	}

	@Test
	void getSubscribers_성공() {
		Long requesterId = 1L;

		User requester = mock(User.class);
		User subscriber1 = mock(User.class);
		User subscriber2 = mock(User.class);
		User subscriber3 = mock(User.class);
		Subscription subscriptionInfo1 = mock(Subscription.class);
		Subscription subscriptionInfo2 = mock(Subscription.class);
		Subscription subscriptionInfo3 = mock(Subscription.class);

		List<Subscription> getSubscribers = new ArrayList<>();
		getSubscribers.add(subscriptionInfo1);
		getSubscribers.add(subscriptionInfo2);
		getSubscribers.add(subscriptionInfo3);

		when(userRepository.findByIdOrElseThrow(requesterId)).thenReturn(requester);
		when(subscriptionRepository.findByReceiver(requester)).thenReturn(getSubscribers);
		when(subscriptionInfo1.getSender()).thenReturn(subscriber1);
		when(subscriptionInfo2.getSender()).thenReturn(subscriber2);
		when(subscriptionInfo3.getSender()).thenReturn(subscriber3);
		when(subscriber1.getUserId()).thenReturn(1L);
		when(subscriber1.getName()).thenReturn("name1");
		when(subscriber1.getEmail()).thenReturn("email1");
		when(subscriber2.getUserId()).thenReturn(2L);
		when(subscriber2.getName()).thenReturn("name1");
		when(subscriber2.getEmail()).thenReturn("email1");
		when(subscriber3.getUserId()).thenReturn(3L);
		when(subscriber3.getName()).thenReturn("name1");
		when(subscriber3.getEmail()).thenReturn("email1");

		List<SubscriptionResponseDto> result = subscriptionService.getSubscribers(requesterId);

		assertEquals(1L, result.get(0).getUserId());
		assertEquals(2L, result.get(1).getUserId());
		assertEquals(3L, result.get(2).getUserId());
		assertEquals("name1", result.get(0).getName());
		assertEquals("email1", result.get(0).getEmail());
	}

	@Test
	void getSubscriptions_성공() {
		Long requesterId = 1L;

		User requester = mock(User.class);
		User subscription1 = mock(User.class);
		User subscription2 = mock(User.class);
		User subscription3 = mock(User.class);
		Subscription subscriptionInfo1 = mock(Subscription.class);
		Subscription subscriptionInfo2 = mock(Subscription.class);
		Subscription subscriptionInfo3 = mock(Subscription.class);

		List<Subscription> subscription = new ArrayList<>();
		subscription.add(subscriptionInfo1);
		subscription.add(subscriptionInfo2);
		subscription.add(subscriptionInfo3);

		when(userRepository.findByIdOrElseThrow(requesterId)).thenReturn(requester);
		when(subscriptionRepository.findBySender(requester)).thenReturn(subscription);
		when(subscriptionInfo1.getReceiver()).thenReturn(subscription1);
		when(subscriptionInfo2.getReceiver()).thenReturn(subscription2);
		when(subscriptionInfo3.getReceiver()).thenReturn(subscription3);
		when(subscription1.getUserId()).thenReturn(1L);
		when(subscription1.getName()).thenReturn("name1");
		when(subscription1.getEmail()).thenReturn("email1");
		when(subscription2.getUserId()).thenReturn(2L);
		when(subscription2.getName()).thenReturn("name1");
		when(subscription2.getEmail()).thenReturn("email1");
		when(subscription3.getUserId()).thenReturn(3L);
		when(subscription3.getName()).thenReturn("name1");
		when(subscription3.getEmail()).thenReturn("email1");

		List<SubscriptionResponseDto> result = subscriptionService.getSubscriptions(requesterId);

		assertEquals(1L, result.get(0).getUserId());
		assertEquals(2L, result.get(1).getUserId());
		assertEquals(3L, result.get(2).getUserId());
		assertEquals("name1", result.get(0).getName());
		assertEquals("email1", result.get(0).getEmail());
	}
}
