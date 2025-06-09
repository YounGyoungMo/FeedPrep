package com.example.feedprep.domain.recommendation.tutor.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.feedback.entity.FeedBack;
import com.example.feedprep.domain.feedback.repository.FeedBackRepository;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.feedbackrequestentity.repository.FeedbackRequestEntityRepository;
import com.example.feedprep.domain.feedbackreview.entity.FeedBackReview;
import com.example.feedprep.domain.feedbackreview.repository.FeedBackReviewRepository;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.techstack.repository.TechStackRepository;
import com.example.feedprep.domain.techstack.repository.UserTechStackRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.querydsl.core.Tuple;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class RecommendationQueryImplTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	FeedBackRepository feedBackRepository;

	@Autowired
	FeedbackRequestEntityRepository feedbackRequestEntityRepository;

	@Autowired
	FeedBackReviewRepository feedBackReviewRepository;

	@Autowired
	UserTechStackRepository userTechStackRepository;

	@Autowired
	DocumentRepository documentRepository;

	@Autowired
	TechStackRepository techStackRepository;

	@Autowired
	RecommendationQuery recommendationQuery;

	@BeforeEach
	void setUp() {
		User student1 = userRepository.save(createStudent("요청 및 리뷰"));
		User student2 = userRepository.save(createStudent("조회용"));

		User tutor1 = userRepository.save(createTutor("튜터1"));
		User tutor2 = userRepository.save(createTutor("튜터2"));
		User tutor3 = userRepository.save(createTutor("튜터3"));
		User tutor4 = userRepository.save(createTutor("튜터4"));
		User tutor5 = userRepository.save(createTutor("튜터5"));

		Document document1 = documentRepository.save(createDocument(student1));

		// 튜터별 피드백 요청 - 완료 상태
		FeedbackRequestEntity feedbackRequestEntity1 = feedbackRequestEntityRepository.save(createFeedbackRequestEntityComplete(student1, tutor1, document1));
		FeedbackRequestEntity feedbackRequestEntity2 = feedbackRequestEntityRepository.save(createFeedbackRequestEntityComplete(student1, tutor1, document1));
		FeedbackRequestEntity feedbackRequestEntity3 = feedbackRequestEntityRepository.save(createFeedbackRequestEntityComplete(student1, tutor1, document1));
		FeedbackRequestEntity feedbackRequestEntity4 = feedbackRequestEntityRepository.save(createFeedbackRequestEntityComplete(student1, tutor2, document1));
		FeedbackRequestEntity feedbackRequestEntity5 = feedbackRequestEntityRepository.save(createFeedbackRequestEntityComplete(student1, tutor3, document1));
		FeedbackRequestEntity feedbackRequestEntity6 = feedbackRequestEntityRepository.save(createFeedbackRequestEntityComplete(student1, tutor4, document1));
		FeedbackRequestEntity feedbackRequestEntity7 = feedbackRequestEntityRepository.save(createFeedbackRequestEntityComplete(student1, tutor5, document1));

		// 튜터별 피드백 요청 - 미완료 상태
		FeedbackRequestEntity feedbackRequestEntity8 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor5, document1));
		FeedbackRequestEntity feedbackRequestEntity9 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor2, document1));
		FeedbackRequestEntity feedbackRequestEntity10 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor2, document1));
		FeedbackRequestEntity feedbackRequestEntity11 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor2, document1));
		FeedbackRequestEntity feedbackRequestEntity12 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor2, document1));
		FeedbackRequestEntity feedbackRequestEntity13 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor3, document1));
		FeedbackRequestEntity feedbackRequestEntity14 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor3, document1));
		FeedbackRequestEntity feedbackRequestEntity15 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor3, document1));
		FeedbackRequestEntity feedbackRequestEntity16 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor4, document1));
		FeedbackRequestEntity feedbackRequestEntity17 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor4, document1));
		FeedbackRequestEntity feedbackRequestEntity18 = feedbackRequestEntityRepository.save(createFeedbackRequestEntity(student1, tutor4, document1));

		TechStack techStack1 = techStackRepository.save(new TechStack("java"));
		TechStack techStack2 = techStackRepository.save(new TechStack("python"));
		TechStack techStack3 = techStackRepository.save(new TechStack("C#"));



		UserTechStack uts1 = userTechStackRepository.save(new UserTechStack(student2, techStack1)); // 학생2 - java
		UserTechStack uts2 = userTechStackRepository.save(new UserTechStack(student2, techStack2)); // 학생2 - python
		UserTechStack uts3 = userTechStackRepository.save(new UserTechStack(student2, techStack3)); // 학생2 - C#



		UserTechStack uts5 = userTechStackRepository.save(new UserTechStack(tutor1, techStack1));   // 튜터1 - java
		UserTechStack uts6 = userTechStackRepository.save(new UserTechStack(tutor1, techStack2));   // 튜터1 - python

		UserTechStack uts7 = userTechStackRepository.save(new UserTechStack(tutor2, techStack1));   // 튜터2 - java
		UserTechStack uts8 = userTechStackRepository.save(new UserTechStack(tutor2, techStack3));   // 튜터2 - C#

		UserTechStack uts9 = userTechStackRepository.save(new UserTechStack(tutor3, techStack1));   // 튜터3 - java

		UserTechStack uts10 = userTechStackRepository.save(new UserTechStack(tutor4, techStack1));  // 튜터4 - java
		UserTechStack uts11 = userTechStackRepository.save(new UserTechStack(tutor4, techStack2));  // 튜터4 - python
		UserTechStack uts12 = userTechStackRepository.save(new UserTechStack(tutor4, techStack3));  // 튜터4 - C#

		UserTechStack uts13 = userTechStackRepository.save(new UserTechStack(tutor5, techStack1));  // 튜터5 - java
		UserTechStack uts14 = userTechStackRepository.save(new UserTechStack(tutor5, techStack2));  // 튜터5 - python



		FeedBack feedBack1 = feedBackRepository.save(createFeedback(feedbackRequestEntity1, "피드백 내용"));
		FeedBack feedBack2 = feedBackRepository.save(createFeedback(feedbackRequestEntity2, "피드백 내용"));
		FeedBack feedBack3 = feedBackRepository.save(createFeedback(feedbackRequestEntity3, "피드백 내용"));
		FeedBack feedBack4 = feedBackRepository.save(createFeedback(feedbackRequestEntity4, "피드백 내용"));
		FeedBack feedBack5 = feedBackRepository.save(createFeedback(feedbackRequestEntity5, "피드백 내용"));
		FeedBack feedBack6 = feedBackRepository.save(createFeedback(feedbackRequestEntity6, "피드백 내용"));
		FeedBack feedBack7 = feedBackRepository.save(createFeedback(feedbackRequestEntity7, "피드백 내용"));



		FeedBackReview feedBackReview1 = feedBackReviewRepository.save(createFeedbackReview(feedBack1, 1));
		FeedBackReview feedBackReview2 = feedBackReviewRepository.save(createFeedbackReview(feedBack2, 1));
		FeedBackReview feedBackReview3 = feedBackReviewRepository.save(createFeedbackReview(feedBack3, 1));
		FeedBackReview feedBackReview4 = feedBackReviewRepository.save(createFeedbackReview(feedBack4, 2));
		FeedBackReview feedBackReview5 = feedBackReviewRepository.save(createFeedbackReview(feedBack5, 3));
		FeedBackReview feedBackReview6 = feedBackReviewRepository.save(createFeedbackReview(feedBack6, 4));
		FeedBackReview feedBackReview7 = feedBackReviewRepository.save(createFeedbackReview(feedBack7, 5));

	}

	public static FeedBackReview createFeedbackReview(FeedBack feedback, int rating) {
		return FeedBackReview.builder()
			.feedback(feedback)
			.Rating(rating)
			.Content("리뷰 내용")
			.build();
	}

	public static FeedBack createFeedback(FeedbackRequestEntity feedbackRequest, String content) {
		return FeedBack.builder()
			.feedBackRequest(feedbackRequest)
			.content(content)
			.build();
	}

	public static FeedbackRequestEntity createFeedbackRequestEntity(User user, User tutor, Document document) {
		return FeedbackRequestEntity.builder()
			.user(user)
			.tutor(tutor)
			.document(document)
			.content("피드백 요청")
			.requestState(RequestState.PENDING)
			.build();
	}

	public static FeedbackRequestEntity createFeedbackRequestEntityComplete(User user, User tutor, Document document) {
		return FeedbackRequestEntity.builder()
			.user(user)
			.tutor(tutor)
			.document(document)
			.content("피드백 요청")
			.requestState(RequestState.COMPLETED)
			.build();
	}

	public static Document createDocument(User user) {
		return Document.builder()
			.fileUrl("문서 url")
			.user(user)
			.build();
	}

	public static User createStudent(String name) {
		return User.builder()
			.name(name)
			.email("이메일")
			.password("비밀번호")  // 테스트용 더미 패스워드
			.address("주소")
			.introduction("소개")
			.role(UserRole.STUDENT)
			.point(0L)
			.build();
	}

	public static User createTutor(String name) {
		return User.builder()
			.name(name)
			.email("이메일")
			.password("비밀번호")
			.address("주소")
			.introduction("소개")
			.role(UserRole.APPROVED_TUTOR)
			.point(1000L)
			.build();
	}

	@Test
	void recommendTutors() {

		List<Tuple> tuples = recommendationQuery.recommendTutors(2L, 0);

		assertThat(tuples).hasSize(4);
		assertThat(tuples.get(0).get(0, Long.class)).isEqualTo(3L);
		assertThat(tuples.get(0).get(1, String.class)).isEqualTo("튜터1");
		assertThat(tuples.get(0).get(2, Double.class)).isEqualTo(1.0);
		assertThat(tuples.get(0).get(3, String.class)).isEqualTo("소개");
		assertThat(tuples.get(0).get(4, Long.class)).isEqualTo(0L);
		assertThat(tuples.get(2).get(0, Long.class)).isEqualTo(6L);
		assertThat(tuples.get(2).get(1, String.class)).isEqualTo("튜터4");
		assertThat(tuples.get(2).get(2, Double.class)).isEqualTo(4.0);
		assertThat(tuples.get(2).get(3, String.class)).isEqualTo("소개");
		assertThat(tuples.get(2).get(4, Long.class)).isEqualTo(3L);

	}
}