package com.example.feedprep.domain.recommendation.tutor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.feedbackreview.entity.FeedBackReview;
import com.example.feedprep.domain.feedbackreview.repository.FeedBackReviewRepository;
import com.example.feedprep.domain.recommendation.tutor.dto.RecommendTutorDto;
import com.example.feedprep.domain.recommendation.tutor.dto.TutorResponseDto;
import com.example.feedprep.domain.recommendation.tutor.repository.RecommendationQuery;
import com.example.feedprep.domain.subscription.dto.SubscriptionResponseDto;
import com.example.feedprep.domain.subscription.entity.Subscription;
import com.example.feedprep.domain.subscription.repository.SubscriptionRepository;
import com.example.feedprep.domain.subscription.service.SubscriptionService;
import com.example.feedprep.domain.techstack.dto.TechStackResponseDto;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.techstack.repository.TechStackRepository;
import com.example.feedprep.domain.techstack.repository.UserTechStackRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TutorRecommendationServiceImpl implements TutorRecommendationService{

	private final RecommendationQuery recommendationQuery;
	private final UserTechStackRepository userTechStackRepository;

	private final SubscriptionService subscriptionService;
	private final FeedBackReviewRepository feedBackReviewRepository;
	private final UserRepository userRepository;

	@Override
	public RecommendTutorDto recommendTutors(Long studentId, int page) {
		if(page < 1){
			throw new CustomException(ErrorCode.PAGE_NOT_FOUND);
		}

		List<Tuple> tutorTuple = recommendationQuery.recommendTutors(studentId, (page-1)*4);

		List<TutorResponseDto> tutorList = tutorTuple.stream().map(TutorResponseDto::new).toList();

		for(TutorResponseDto dto : tutorList) {
			List<UserTechStack> techStack = userTechStackRepository.findUserTechStackByUser_UserId(dto.getTutorId());
			dto.setTechStacks(techStack.stream().map(TechStackResponseDto::new).map(TechStackResponseDto::getTechStack).toList());
		}

		List<TutorResponseDto> subscribedTutorList = subscriptionTutor(studentId);

		return new RecommendTutorDto(subscribedTutorList, tutorList);
	}

	private List<TutorResponseDto> subscriptionTutor(Long studentId){
		List<SubscriptionResponseDto> subscriptions = subscriptionService.getSubscriptions(studentId);

		List<TutorResponseDto> subscribedTutorList = new ArrayList<>();

		for(SubscriptionResponseDto dto :subscriptions){
			Double tutorRating = feedBackReviewRepository
				.findAverageRatingByUserId(dto.getUserId());

			List<UserTechStack> tutorTechStack
				= userTechStackRepository
				.findUserTechStackByUser_UserId(dto.getUserId());

			User tutorInfo = userRepository.findByIdOrElseThrow(dto.getUserId());

			TutorResponseDto tutorResponseDto = new TutorResponseDto(tutorInfo, tutorRating, tutorTechStack);
			subscribedTutorList.add(tutorResponseDto);
		}

		return subscribedTutorList;
	}
}
