package com.example.feedprep.domain.techstack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.techstack.dto.TechStackResponseDto;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.techstack.repository.TechStackRepository;
import com.example.feedprep.domain.techstack.repository.UserTechStackRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TechStackServiceImplTest {
	@Mock
	private UserTechStackRepository userTechStackRepository;
	@Mock
	private TechStackRepository techStackRepository;
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private TechStackServiceImpl techStackService;

	@Test
	void getTechStackList_성공() {

		TechStack techStack1 = mock(TechStack.class);
		TechStack techStack2 = mock(TechStack.class);

		List<TechStack> techStackList = List.of(techStack1, techStack2);

		when(techStackRepository.findAll()).thenReturn(techStackList);
		when(techStack1.getTechId()).thenReturn(1L);
		when(techStack1.getTechStack()).thenReturn("C#");
		when(techStack2.getTechId()).thenReturn(2L);
		when(techStack2.getTechStack()).thenReturn("Spring boot");

		List<TechStackResponseDto> allTechStack = techStackService.getTechStackList();

		assertEquals("C#", allTechStack.get(0).getTechStack());
		assertEquals(1L, allTechStack.get(0).getTechStackId());
		assertEquals("Spring boot", allTechStack.get(1).getTechStack());
		assertEquals(2L, allTechStack.get(1).getTechStackId());
	}

	@Test
	void addTechStack_성공() {
		Long requesterId = 1L;
		Long techId = 1L;

		TechStack techStack = mock(TechStack.class);
		User user = mock(User.class);

		when(userRepository.findByIdOrElseThrow(requesterId)).thenReturn(user);
		when(techStackRepository.findByIdOrElseThrow(techId)).thenReturn(techStack);
		when(userTechStackRepository.existsByTechStackAndUser(techStack, user)).thenReturn(false);

		assertDoesNotThrow(() -> techStackService.addTechStack(requesterId, techId));
		verify(userTechStackRepository, times(1)).save(any());


	}

	@Test
	void addTechStack_실패_중복된_기술스택_추가() {
		Long requesterId = 1L;
		Long techId = 1L;

		TechStack techStack = mock(TechStack.class);
		User user = mock(User.class);

		when(userRepository.findByIdOrElseThrow(requesterId)).thenReturn(user);
		when(techStackRepository.findByIdOrElseThrow(techId)).thenReturn(techStack);
		when(userTechStackRepository.existsByTechStackAndUser(techStack, user)).thenReturn(true);

		CustomException exception = assertThrows(CustomException.class, () -> {
			techStackService.addTechStack(requesterId, techId);
		});

		assertEquals(ErrorCode.DUPLICATE_TECH_STACK, exception.getErrorCode());
	}

	@Test
	void getMyTechStackList_성공() {
		Long userId = 1L;
		TechStack techStack1 = mock(TechStack.class);
		TechStack techStack2 = mock(TechStack.class);
		User user1 = mock(User.class);
		User user2 = mock(User.class);
		UserTechStack userTechStack1 = new UserTechStack(user1, techStack1);
		UserTechStack userTechStack2 = new UserTechStack(user2, techStack2);

		List<UserTechStack> techStackList = List.of(userTechStack1, userTechStack2);

		when(userTechStackRepository.findUserTechStackByUser_UserId(userId)).thenReturn(techStackList);
		when(techStack1.getTechStack()).thenReturn("C#");
		when(techStack2.getTechStack()).thenReturn("Spring boot");
		when(techStack1.getTechId()).thenReturn(1L);
		when(techStack2.getTechId()).thenReturn(2L);

		List<TechStackResponseDto> myTechStack = techStackService.getMyTechStackList(userId);

		assertEquals("C#", myTechStack.get(0).getTechStack());
		assertEquals(1L, myTechStack.get(0).getTechStackId());
		assertEquals("Spring boot", myTechStack.get(1).getTechStack());
		assertEquals(2L, myTechStack.get(1).getTechStackId());
	}

	@Test
	void removeTechStack_성공() {
		Long requesterId = 1L;
		Long relationId = 1L;

		UserTechStack myTechStack = mock(UserTechStack.class);
		User user = mock(User.class);

		when(userTechStackRepository.findByIdOrElseThrow(relationId)).thenReturn(myTechStack);
		when(myTechStack.getUser()).thenReturn(user);
		when(user.getUserId()).thenReturn(1L);

		assertDoesNotThrow(() -> techStackService.removeTechStack(requesterId, relationId));
		verify(userTechStackRepository, times(1)).delete(any());
	}

	@Test
	void removeTechStack_실패() {
		Long requesterId = 1L;
		Long relationId = 1L;

		UserTechStack myTechStack = mock(UserTechStack.class);
		User user = mock(User.class);

		when(userTechStackRepository.findByIdOrElseThrow(relationId)).thenReturn(myTechStack);
		when(myTechStack.getUser()).thenReturn(user);
		when(user.getUserId()).thenReturn(2L);

		CustomException exception = assertThrows(CustomException.class, () -> {
			techStackService.removeTechStack(requesterId, relationId);
		});

		assertEquals(ErrorCode.UNAUTHORIZED_TECH_STACK_DELETION, exception.getErrorCode());
	}
}