package com.example.feedprep.domain.matching.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.feedprep.domain.matching.dto.MatchingDto;
import com.example.feedprep.domain.matching.repository.MatchingQuery;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.techstack.repository.UserTechStackRepository;
import com.querydsl.core.Tuple;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TutorMatchingServiceImplTest {

	@Mock
	private MatchingQuery matchingQuery;

	@Mock
	private UserTechStackRepository userTechStackRepository;

	@InjectMocks
	private TutorMatchingServiceImpl tutorMatchingService;

	@Test
	void tutorMatching() {
		Long studentId = 1L;
		int page = 0;

		Tuple tupleMock = mock(Tuple.class);
		MatchingDto dto = new MatchingDto(tupleMock);
		when(tupleMock.get(0, Long.class)).thenReturn(101L);

		List<Tuple> mockTuples = List.of(tupleMock);
		when(matchingQuery.getTutor(studentId, page)).thenReturn(mockTuples);

		TechStack javaStack = mock(TechStack.class);
		when(javaStack.getTechStack()).thenReturn("Java");

		UserTechStack uts = mock(UserTechStack.class);
		when(uts.getTechStack()).thenReturn(javaStack);

		when(userTechStackRepository.findUserTechStackByUser_UserId(101L))
			.thenReturn(List.of(uts));

		List<MatchingDto> result = tutorMatchingService.tutorMatching(studentId, page);

		assertEquals(1, result.size());
		MatchingDto resultDto = result.get(0);
		assertEquals(101L, resultDto.getTutorId());
		assertEquals(List.of("Java"), resultDto.getTechStacks());
	}
}