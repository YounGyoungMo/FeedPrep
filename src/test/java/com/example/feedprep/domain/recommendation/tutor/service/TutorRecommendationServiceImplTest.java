package com.example.feedprep.domain.recommendation.tutor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.feedprep.domain.feedbackreview.repository.FeedBackReviewRepository;
import com.example.feedprep.domain.recommendation.tutor.dto.RecommendTutorDto;
import com.example.feedprep.domain.recommendation.tutor.dto.TutorResponseDto;
import com.example.feedprep.domain.recommendation.tutor.repository.RecommendationQuery;
import com.example.feedprep.domain.subscription.service.SubscriptionService;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.techstack.repository.UserTechStackRepository;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.querydsl.core.Tuple;

@ExtendWith(MockitoExtension.class)
class TutorRecommendationServiceImplTest {

	@Mock
	RecommendationQuery recommendationQuery;

	@Mock
	UserTechStackRepository userTechStackRepository;

	@Mock
	SubscriptionService subscriptionService;

	@Mock
	FeedBackReviewRepository feedBackReviewRepository;

	@Mock
	UserRepository userRepository;

	@InjectMocks
	TutorRecommendationServiceImpl tutorRecommendationService;

	@Test
	void recommendTutors() {
		Long studentId = 1L;
		int page = 1;
		Tuple tuple1 = mock(Tuple.class);
		Tuple tuple2 = mock(Tuple.class);
		Tuple tuple3 = mock(Tuple.class);

		UserTechStack userTechStack1 = mock(UserTechStack.class);
		UserTechStack userTechStack2 = mock(UserTechStack.class);
		UserTechStack userTechStack3 = mock(UserTechStack.class);

		TechStack techStack1 = mock(TechStack.class);
		TechStack techStack2 = mock(TechStack.class);
		TechStack techStack3 = mock(TechStack.class);


		when(tuple1.get(0, Long.class)).thenReturn(2L);
		when(tuple1.get(1, String.class)).thenReturn("이름1");
		when(tuple1.get(2, Double.class)).thenReturn(3.3);
		when(tuple1.get(3, String.class)).thenReturn("소개1");

		when(tuple2.get(0, Long.class)).thenReturn(3L);
		when(tuple2.get(1, String.class)).thenReturn("이름2");
		when(tuple2.get(2, Double.class)).thenReturn(2.3);
		when(tuple2.get(3, String.class)).thenReturn("소개2");

		when(tuple3.get(0, Long.class)).thenReturn(4L);
		when(tuple3.get(1, String.class)).thenReturn("이름3");
		when(tuple3.get(2, Double.class)).thenReturn(1.3);
		when(tuple3.get(3, String.class)).thenReturn("소개3");

		when(userTechStack1.getTechStack()).thenReturn(techStack1);
		when(userTechStack2.getTechStack()).thenReturn(techStack2);
		when(userTechStack3.getTechStack()).thenReturn(techStack3);

		when(techStack1.getTechStack()).thenReturn("Java");
		when(techStack2.getTechStack()).thenReturn("Python");
		when(techStack3.getTechStack()).thenReturn("C#");

		List<Tuple> tutorTuple = List.of(tuple1, tuple2, tuple3);
		List<UserTechStack> tutorTechStack1 = List.of(userTechStack1, userTechStack2);
		List<UserTechStack> tutorTechStack2 = List.of(userTechStack1, userTechStack3);
		List<UserTechStack> tutorTechStack3 = List.of(userTechStack1);

		when(userTechStackRepository.findUserTechStackByUser_UserId(2L)).thenReturn(tutorTechStack1);
		when(userTechStackRepository.findUserTechStackByUser_UserId(3L)).thenReturn(tutorTechStack2);
		when(userTechStackRepository.findUserTechStackByUser_UserId(4L)).thenReturn(tutorTechStack3);
		when(recommendationQuery.recommendTutors(studentId, 0)).thenReturn(tutorTuple);

		RecommendTutorDto result = tutorRecommendationService.recommendTutors(studentId, page);

		assertEquals(2L, result.getTutorResponseDto().get(0).getTutorId());
		assertEquals("Java", result.getTutorResponseDto().get(0).getTechStacks().get(0));
		assertEquals("Python", result.getTutorResponseDto().get(0).getTechStacks().get(1));

	}
}