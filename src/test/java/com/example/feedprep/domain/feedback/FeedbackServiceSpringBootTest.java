package com.example.feedprep.domain.feedback;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.feedback.dto.request.FeedbackWriteRequestDto;
import com.example.feedprep.domain.feedback.dto.response.FeedbackResponseDto;
import com.example.feedprep.domain.feedback.service.FeedbackService;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.FeedbackRequestEntityResponseDto;
import com.example.feedprep.domain.feedbackrequestentity.service.FeedbackRequestService;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FeedbackServiceSpringBootTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private FeedbackRequestService feedbackRequestService;
	@Autowired
	private FeedbackService feedbackService;

	private List<User> tutors;
	private List<User> users;
	private Document document;
	private List<FeedbackRequestDto> requestDtos;
	private FeedbackRequestEntityResponseDto newRequest;

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

	@BeforeEach
	void setup(){
		// 튜터 4명
		tutors = List.of(
			new User("Tutor1", "tutor1@naver.com", "tester1234", UserRole.APPROVED_TUTOR),//1L
			new User("Tutor2", "tutor2@naver.com", "tester1234", UserRole.APPROVED_TUTOR),//2L
			new User("Tutor3", "tutor3@naver.com", "tester1234", UserRole.APPROVED_TUTOR),//3L
			new User("Tutor4", "tutor4@naver.com", "tester1234", UserRole.APPROVED_TUTOR) //4L
		);
		userRepository.saveAll(tutors);

		// 유저 5명
		users = List.of(
			new User("Paragon0", "p0@naver.com", "tester1234", UserRole.STUDENT),//5L
			new User("Paragon1", "p1@naver.com", "tester1234", UserRole.STUDENT),//6L
			new User("Paragon2", "p2@naver.com", "tester1234", UserRole.STUDENT),//7L
			new User("Paragon3", "p3@naver.com", "tester1234", UserRole.STUDENT),//8L
			new User("Paragon4", "p4@naver.com", "tester1234", UserRole.STUDENT) //9L
		);
		userRepository.saveAll(users);

		// Document 생성
		document = new Document(users.get(0), "api/ef/?");
		documentRepository.save(document);

		requestDtos = List.of(
			new FeedbackRequestDto(1L, 1L, "paragon"),
			new FeedbackRequestDto(2L, 1L, "paragon1"),
			new FeedbackRequestDto(3L, 1L, "paragon3"),
			new FeedbackRequestDto(4L, 1L, "paragon4")

		);
		newRequest = feedbackRequestService.createRequest( users.get(0).getUserId(), requestDtos.get(0));
	}

	@Transactional
	@Test
	public void 피드백_작성(){


		feedbackRequestService.acceptRequest(tutors.get(0).getUserId(), 1L);

		FeedbackWriteRequestDto requestDto
			= new FeedbackWriteRequestDto("내용");

		long start = System.currentTimeMillis();
		FeedbackResponseDto response = feedbackService.createFeedback(tutors.get(0).getUserId(), 1L, requestDto);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력

		try {
			String json = mapper.writeValueAsString(response); // 전체 객체를 JSON 변환
			System.out.println(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	@Transactional
	@Test
	public void 피드백_수정(){

		feedbackRequestService.acceptRequest(tutors.get(0).getUserId(), 1L);

		//피드백 작성 하기
		FeedbackWriteRequestDto requestDto =
			new FeedbackWriteRequestDto("직성 완료했습니다.");

		//피드백 작성 완료 저장
		long start = System.currentTimeMillis();
		FeedbackResponseDto response = feedbackService.createFeedback(tutors.get(0).getUserId(), 1L, requestDto);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력

		try {
			String json = mapper.writeValueAsString(response); // 전체 객체를 JSON 변환
			System.out.println(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}


}
