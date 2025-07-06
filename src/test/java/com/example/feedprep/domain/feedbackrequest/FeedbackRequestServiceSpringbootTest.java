package com.example.feedprep.domain.feedbackrequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRejectRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.TutorFeedbackResponseDetailsDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.UserFeedbackRequestDetailsDto;
import com.example.feedprep.domain.feedbackrequestentity.dto.response.FeedbackRequestDetailsDto;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.feedbackrequestentity.repository.FeedbackRequestEntityRepository;
import com.example.feedprep.domain.feedbackrequestentity.service.FeedbackRequestService;
import com.example.feedprep.domain.point.repository.PointRepository;
import com.example.feedprep.domain.point.service.PointService;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//테스트 마다 DB 깨끗하게 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedbackRequestServiceSpringbootTest {
	@Autowired
	private FeedbackRequestEntityRepository feedbackRequestEntityRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private FeedbackRequestService feedbackRequestService;
	@Autowired
	private PointRepository pointRepository;
	@Autowired
	private PointService pointService;

	private List<User> tutors;
	private List<User> users;
	private Document document;
	private List<FeedbackRequestDto> requestDtos;

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

		pointRepository.deleteAll();

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

		for(int i= 0; i< users.size(); i++){
			String PaymentId = "pid_" + UUID.randomUUID();
			pointService.pointCharge(users.get(i).getUserId(), PaymentId, 100000000);
		}
		requestDtos = List.of(
			new FeedbackRequestDto(1L, 1L, "paragon"),
			new FeedbackRequestDto(2L, 1L, "paragon1"),
			new FeedbackRequestDto(3L, 1L, "paragon3"),
			new FeedbackRequestDto(4L, 1L, "paragon4")

		);
	}

	@Transactional
	@Test
	public void 요청하기(){


		long start = System.currentTimeMillis();
		UserFeedbackRequestDetailsDto userFeedbackRequestDetailsDto =
			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(0));
		long end= System.currentTimeMillis();
		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회

		assertNotNull(userFeedbackRequestDetailsDto);
		assertEquals("paragon", userFeedbackRequestDetailsDto.getContent());

		showResult(userFeedbackRequestDetailsDto);


	}

	@Transactional
	@Test
	public void 유저_아이디_기본_조회테스트() {

		for(int  i =0; i <requestDtos.size(); i++){
			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
		}
		RequestState requestState = RequestState.fromNumber(0);
		long start = System.currentTimeMillis();
		List<UserFeedbackRequestDetailsDto> getRequests
			= feedbackRequestService.getRequests(users.get(4).getUserId(), null, null, null, requestState ,0, 20);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		assertNotNull(getRequests);
		assertEquals(4, getRequests.size());
		showResult(getRequests);

	}

	@Transactional
	@Test
	public void 유저_아이디와_튜터아이디_기본_조회테스트() {

		for(int  i =0; i <requestDtos.size(); i++){
			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
		}
		long start = System.currentTimeMillis();
		List<UserFeedbackRequestDetailsDto> getRequests =
		feedbackRequestService.getRequests(users.get(4).getUserId(), 2L, null, null, null, 0,20);
		long end= System.currentTimeMillis();

		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		assertNotNull(getRequests);
		assertEquals(1, getRequests.size());
		assertEquals(2L, getRequests.get(0).getTutorId());
		showResult(getRequests);
	}

	@Transactional
	@Test
	public void 유저_아이디와_문서아이디_기본_조회테스트() {

		for(int  i =0; i <requestDtos.size(); i++){
			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
		}
		RequestState requestState = RequestState.fromNumber(0);

		long start = System.currentTimeMillis();
		List<UserFeedbackRequestDetailsDto> getRequests =
		feedbackRequestService.getRequests(users.get(4).getUserId(), null, 1L, null, requestState, 0, 20);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력

		assertNotNull(getRequests);
		assertEquals(4, getRequests.size());
		assertEquals(1L, getRequests.get(2).getDocumentId());
		showResult(getRequests);
	}
	@Transactional
	@Test
	public void 유저_아이디와_어느_조건_상관없이_Cancele제외_기본_조회테스트() {

		//given
		FeedbackRequestDto canceledRequestDto = new FeedbackRequestDto(3L, 1L, "Text");
		FeedbackRequestEntity alreadyExistRequest
			= new FeedbackRequestEntity(canceledRequestDto, users.get(4), users.get(2),document);
		alreadyExistRequest .updateRequestState(RequestState.CANCELED);

		feedbackRequestEntityRepository.save(alreadyExistRequest );

		for(int  i =0; i <requestDtos.size(); i++){
			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
		}
		RequestState requestState = RequestState.fromNumber(0);

		//when
		long start = System.currentTimeMillis();
		List<UserFeedbackRequestDetailsDto> getRequests =
			feedbackRequestService.getRequests(users.get(4).getUserId(), null, 1L, null,requestState, 0, 20);

		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		//then
		assertNotNull(getRequests);
		assertEquals(4, getRequests.size());
		assertFalse(getRequests.stream()
			.anyMatch(req -> req.getRequestState() == RequestState.CANCELED));
		showResult(getRequests);
	}

	@Transactional
	@Test
	public void 유저_아이디와_날짜_조회테스트() {


		FeedbackRequestDto canceledRequestDto = new FeedbackRequestDto(3L, 1L, "Text");
		FeedbackRequestEntity alreadyExistReuest
			= new FeedbackRequestEntity(canceledRequestDto, users.get(4), users.get(2),document);

		alreadyExistReuest.updateRequestState(RequestState.CANCELED);

		feedbackRequestEntityRepository.save(alreadyExistReuest);

		for(int  i =0; i <requestDtos.size(); i++){
			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
		}
		RequestState requestState = RequestState.fromNumber(0);
		LocalDateTime month = null;

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
		YearMonth ym = YearMonth.parse("2025-05", dateTimeFormatter);
		month = ym.atDay(1).atStartOfDay();  // LocalDateTime으로 변환

		long start = System.currentTimeMillis();
		List<UserFeedbackRequestDetailsDto> getRequests =
			feedbackRequestService.getRequests(users.get(4).getUserId(), null, 1L, month ,requestState, 0, 20);

		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		//then
		assertNotNull(getRequests);
		assertEquals(0, getRequests.size());
		showResult(getRequests);
	}

	@Transactional
	@Test
	public  void 요청_수정하기(){

		feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(0));

		FeedbackRequestDto UpdateRequestDto = new FeedbackRequestDto(1L, 1L, "수정된 피드백 요청 내용");
		long start = System.currentTimeMillis();
		UserFeedbackRequestDetailsDto updateUserFeedbackRequestDetailsDto =
			feedbackRequestService.updateRequest( users.get(4).getUserId() ,1L, UpdateRequestDto);

		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
		//then
		assertNotNull(updateUserFeedbackRequestDetailsDto);
		assertEquals("수정된 피드백 요청 내용", updateUserFeedbackRequestDetailsDto.getContent());
		showResult(updateUserFeedbackRequestDetailsDto);

	}

	@Transactional
	@Test
	public void 요청_취소하기(){

		feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(0));

		long start = System.currentTimeMillis();
		UserFeedbackRequestDetailsDto userFeedbackRequestDetailsDto
			=  feedbackRequestService.cancelRequest(users.get(4).getUserId(),1L);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		//then
		assertNotNull(userFeedbackRequestDetailsDto);
		assertEquals(RequestState.CANCELED, userFeedbackRequestDetailsDto.getRequestState());
		showResult(userFeedbackRequestDetailsDto);
	}


	@Transactional
	@Test
	public void 튜터가_단건_상세조회_테스트(){

		for(int  i =0; i <requestDtos.size(); i++){
			feedbackRequestService.createRequest( users.get(1).getUserId(), requestDtos.get(i));
		}
		Long userId = tutors.get(1).getUserId();
		long start = System.currentTimeMillis();
		FeedbackRequestDetailsDto getRequest =
			feedbackRequestService.getFeedbackRequest(userId,2L);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
		//then
		assertNotNull(getRequest);
		assertEquals(2L, getRequest.getTutorId());
		assertEquals(RequestState.PENDING, getRequest.getRequestState());
		showResult(getRequest );
	}
	@Transactional
	@Test
	public void 유저가_단건_상세조회_테스트(){

		for(int  i =0; i <requestDtos.size(); i++){
			feedbackRequestService.createRequest( users.get(1).getUserId(), requestDtos.get(i));
		}
		Long userId = users.get(1).getUserId();
		long start = System.currentTimeMillis();
		FeedbackRequestDetailsDto getRequest =
			feedbackRequestService.getFeedbackRequest(userId,2L);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
		//then
		assertNotNull(getRequest);
		assertEquals(6L, getRequest.getUserId());
		assertEquals(RequestState.PENDING, getRequest.getRequestState());
		showResult(getRequest );
	}
	@Transactional
	@Test
	public void 다건_조회_테스트(){

		feedbackRequestService.createRequest( users.get(0).getUserId(), requestDtos.get(3));
		feedbackRequestService.createRequest( users.get(1).getUserId(), requestDtos.get(3));
		feedbackRequestService.createRequest( users.get(2).getUserId(), requestDtos.get(3));
		feedbackRequestService.createRequest( users.get(3).getUserId(), requestDtos.get(3));

		long start = System.currentTimeMillis();
		List<TutorFeedbackResponseDetailsDto> getRequests =  feedbackRequestService.getFeedbackRequests(tutors.get(3).getUserId(),0, 20);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력

		assertNotNull(getRequests);
		showResult(getRequests);
	}

	@Transactional
	@Test
	public void 피드백_거절(){

		FeedbackRequestDto testRequestDto = new FeedbackRequestDto(tutors.get(2).getUserId(), 1L, "Text");
		feedbackRequestService.createRequest( users.get(1).getUserId(), testRequestDto);
		//튜터는 특정 피드백 요청에 대해 거절 사유등록해서 거절을 승인한다.

		FeedbackRejectRequestDto feedbackRejectRequestDto =
			new FeedbackRejectRequestDto("OO 사유로 거절함.");

		long start = System.currentTimeMillis();
		TutorFeedbackResponseDetailsDto response =
			feedbackRequestService.rejectFeedbackRequest(tutors.get(2).getUserId(),1L,5, feedbackRejectRequestDto);
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		assertNotNull(response);
		assertEquals(RequestState.REJECTED, response.getRequestState());
		showResult(response);
	}
	@Transactional
	@Test
	public void 피드백_수락(){

		 UserFeedbackRequestDetailsDto entityResponseDto =
			 feedbackRequestService.createRequest( users.get(4).getUserId(), requestDtos.get(0));

		long start = System.currentTimeMillis();
		TutorFeedbackResponseDetailsDto response =
			feedbackRequestService.acceptRequest(tutors.get(0).getUserId(), entityResponseDto.getId());
		long end= System.currentTimeMillis();
		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회

		assertNotNull(response);
		assertEquals(RequestState.IN_PROGRESS, response.getRequestState());
		showResult(response);
	}



}
