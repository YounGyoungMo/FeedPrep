package com.example.feedprep.domain.feedback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.feedback.dto.request.FeedbackWriteRequestDto;
import com.example.feedprep.domain.feedback.dto.response.FeedbackResponseDto;
import com.example.feedprep.domain.feedback.service.FeedbackService;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.UserFeedbackRequestDetailsDto;
import com.example.feedprep.domain.feedbackrequestentity.service.FeedbackRequestService;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedbackServiceSpringBootTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private FeedbackRequestService feedbackRequestService;
	@Autowired
	private FeedbackService feedbackService;

	private User tutors;
	private User users;
	private Document document;
	private FeedbackRequestDto requestDtos;

	private void showResult(Object dto){
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력

		String json = null; // 전체 객체를 JSON 변환
		try {
			json = mapper.writeValueAsString(dto);
			System.out.println(json);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	UserFeedbackRequestDetailsDto newRequest;
	@BeforeEach
	void setup(){

		// 튜터 4명
		tutors = new User("Tutor1", "tutor1@naver.com", "tester1234", UserRole.APPROVED_TUTOR);
		// 유저 5명
		users = new User("Paragon0", "p0@naver.com", "tester1234", UserRole.STUDENT);
		userRepository.save(tutors);
		userRepository.save(users);

		// Document 생성
		document = new Document(users, "api/ef/?");
		documentRepository.save(document);

		requestDtos = new FeedbackRequestDto(1L, 1L, "paragon");
		newRequest = feedbackRequestService.createRequest( users.getUserId(), requestDtos);

	}

	@Test
	public void 피드백_작성(){
		feedbackRequestService.acceptRequest(1L, 1L);

		FeedbackWriteRequestDto requestDto
			= new FeedbackWriteRequestDto("내용");

		long start = System.currentTimeMillis();
		FeedbackResponseDto response = feedbackService.createFeedback(1L, 1L, requestDto);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
		assertNotNull(response);
		showResult(response);
	}
	@Transactional
	@Test
	public void 피드백_수정(){

		feedbackRequestService.acceptRequest(1L, newRequest.getId());

		//피드백 작성 하기
		FeedbackWriteRequestDto requestDto =
			new FeedbackWriteRequestDto("직성 완료했습니다.");

		feedbackService.createFeedback(1L, 1L, requestDto);

		FeedbackWriteRequestDto updateRequestDto =
			new FeedbackWriteRequestDto("수정 완료했습니다.");

		//피드백 작성 완료 저장
		long start = System.currentTimeMillis();
		FeedbackResponseDto response = feedbackService.updateFeedback(1L, newRequest.getId(),updateRequestDto );
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

        assertNotNull(response);
		assertEquals(updateRequestDto.getContent(), response.getContent());
		showResult(response);
	}


}
