package com.example.feedprep.domain.feedbackreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.feedback.dto.request.FeedbackWriteRequestDto;
import com.example.feedprep.domain.feedback.service.FeedbackServiceImpl;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.service.FeedbackRequestServiceImpl;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewRequestDto;
import com.example.feedprep.domain.feedbackreview.service.FeedbackReviewServiceImpl;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
public class FeedbackReviewSpringbootTest  {

	@Autowired
	UserRepository userRepository;
	@Autowired
	DocumentRepository documentRepository;
	@Autowired
	FeedbackRequestServiceImpl feedbackRequestService;
	@Autowired
	FeedbackServiceImpl feedbackService;
	@Autowired
	FeedbackReviewServiceImpl feedbackReviewService;

	@Autowired
	RedisTemplate<String, Double> redisTemplate; // 상속 받은 Redis
	@Autowired
	@Qualifier("stringRedisTemplate")
	RedisTemplate<String, String > statusTemplate; // 상속 받은 Redis
	public List<User> users;

	@BeforeEach
	void setup(TestInfo testInfo) {

		if(!testInfo.getDisplayName().equals("캐시_상태_검증()")){
			users = List.of(
				new User("Astra", "Test@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
				new User("paragon1", "Test1@naver.com", "tester1234", UserRole.STUDENT),
				new User("paragon2", "Test2@naver.com", "tester1234", UserRole.STUDENT),
				new User("paragon3", "Test3@naver.com", "tester1234", UserRole.STUDENT)
			);
			userRepository.saveAll(users);

			List<Document> documents = new ArrayList<>();
			for(int i =1; i< users.size(); i++){
				documents.add( new Document(users.get(i), "test/url"));
			}
			documentRepository.saveAll(documents);
			List<FeedbackRequestDto> requestDtos = new ArrayList<>();
			for(int i = 0; i< 3; i++){
				requestDtos.add(new FeedbackRequestDto(users.get(0).getUserId(), documents.get(i).getDocumentId(),"의뢰 드립니다."));
			}
			//유저 신청
			feedbackRequestService.createRequest( users.get(1).getUserId(), requestDtos.get(0));
			feedbackRequestService.createRequest( users.get(2).getUserId(), requestDtos.get(1));
			feedbackRequestService.createRequest( users.get(3).getUserId(), requestDtos.get(2));

			Long user1 = users.get(1).getUserId();
			feedbackRequestService.acceptRequest(user1, 1L);
			feedbackRequestService.acceptRequest(users.get(2).getUserId(), 2L);
			feedbackRequestService.acceptRequest(users.get(3).getUserId(), 3L);
			//튜터 피드백 작성
			FeedbackWriteRequestDto feedbackWriteRequestDto =new FeedbackWriteRequestDto("작성 완료!");
			feedbackService.createFeedback(users.get(0).getUserId(),1L, feedbackWriteRequestDto);
			feedbackService.createFeedback(users.get(0).getUserId(),2L, feedbackWriteRequestDto);
			feedbackService.createFeedback(users.get(0).getUserId(),3L, feedbackWriteRequestDto);

			//유저
			Random random =new Random();

			feedbackReviewService.createReview(
				users.get(1).getUserId(),
				1L,
				new FeedbackReviewRequestDto(random.nextInt(0,6),"감사합니다."));
			feedbackReviewService.createReview(
				users.get(2).getUserId(),
				2L,
				new FeedbackReviewRequestDto(random.nextInt(0,6),"감사합니다."));
			feedbackReviewService.createReview(
				users.get(3).getUserId(),
				3L,
				new FeedbackReviewRequestDto(random.nextInt(0,6),"감사합니다."));
		}

	}

	@Test
	void 평점_출력(){
		// given
		Long tutorId = users.get(0).getUserId(); // Astra
		feedbackReviewService.updateRatings();

		// when
		Double cachedRating = redisTemplate.opsForValue().get("rating:" + tutorId);

		// then
		assertNotNull(cachedRating);
		System.out.println("Rating" + cachedRating);

	}

	@Test
	void 캐시_상태_검증(){
		String value = statusTemplate.opsForValue().get("status:updateRatings");
		assertEquals("done", value);
	}
}
