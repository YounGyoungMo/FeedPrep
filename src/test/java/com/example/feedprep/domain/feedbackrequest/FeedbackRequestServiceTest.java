// package com.example.feedprep.domain.feedbackrequest;
//
// import java.time.LocalDateTime;
// import java.time.YearMonth;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.actuate.metrics.data.DefaultRepositoryTagsProvider;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;
// import com.example.feedprep.domain.document.entity.Document;
// import com.example.feedprep.domain.document.repository.DocumentRepository;
// import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
// import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRejectRequestDto;
// import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
// import com.example.feedprep.domain.feedbackrequestentity.dto.response.FeedbackRequestEntityResponseDto;
// import com.example.feedprep.domain.feedbackrequestentity.dto.response.FeedbackResponseDetailsDto;
// import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
// import com.example.feedprep.domain.feedbackrequestentity.repository.FeedbackRequestEntityRepository;
// import com.example.feedprep.domain.feedbackrequestentity.service.FeedbackRequestService;
// import com.example.feedprep.domain.user.entity.User;
// import com.example.feedprep.domain.user.enums.UserRole;
// import com.example.feedprep.domain.user.repository.UserRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//
// @ActiveProfiles("local")
// @SpringBootTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// public class FeedbackRequestServiceTest {
// 	@Autowired
// 	private FeedbackRequestEntityRepository feedbackRequestEntityRepository;
// 	@Autowired
// 	private UserRepository userRepository;
// 	@Autowired
// 	private DocumentRepository documentRepository;
// 	@Autowired
// 	private FeedbackRequestService feedbackRequestService;
// 	private DefaultRepositoryTagsProvider defaultRepositoryTagsProvider;
//
// 	public List<User> UserSetting (){
// 		return List.of(
// 			 new User("Astra1", "Test1@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			 new User("Astra2", "Test2@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			 new User("Astra3", "Test3@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			 new User("Astra4", "Test4@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			 new User("paragon", "Test3@naver.com", "tester1234", UserRole.STUDENT)
// 		 );
// 	}
//
// 	@Transactional
// 	@Test
// 	public void 요청하기(){
//
// 		List<User> users =UserSetting ();
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(4), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		FeedbackRequestDto requestDto = new FeedbackRequestDto(users.get(2).getUserId(), 1L, "Text");
//
// 		//중복 방지 테스트 생성되는지 확인할려면 주석 처리.
// 		// FeedbackRequestEntity alreadyExistReuest = new FeedbackRequestEntity(requestDto, users.get(5), users.get(2),doc);
// 		// alreadyExistReuest.updateRequestState(RequestState.PENDING);
// 	    // feedbackRequestEntityRepository.save(alreadyExistReuest);
//
// 		long start = System.currentTimeMillis();
// 		FeedbackRequestEntityResponseDto feedbackRequestEntityResponseDto =
// 			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDto);
// 		long end= System.currentTimeMillis();
// 		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		String json = null; // 전체 객체를 JSON 변환
// 		try {
// 			json = mapper.writeValueAsString(feedbackRequestEntityResponseDto);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
//
// 	@Transactional
// 	@Test
// 	public void 유저_아이디_기본_조회테스트() {
//
// 		List<User> users = UserSetting();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(4), "api/ef/?");
// 		documentRepository.save(doc);
// 		List<FeedbackRequestDto> requestDtos = List.of(
// 			new FeedbackRequestDto(1L, 1L, "Text"),
// 			new FeedbackRequestDto(2L, 1L, "Text"),
// 			new FeedbackRequestDto(3L, 1L, "Text"),
// 			new FeedbackRequestDto(4L, 1L, "Text")
//
// 		);
// 		for(int  i =0; i <requestDtos.size(); i++){
// 			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
// 		}
// 		RequestState requestState = RequestState.fromNumber(0);
// 		long start = System.currentTimeMillis();
// 		List<FeedbackRequestEntityResponseDto> getRequests
// 			= feedbackRequestService.getRequests(users.get(4).getUserId(), null, null, null, requestState ,0, 20);
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		String json = null; // 전체 객체를 JSON 변환
// 		try {
// 			json = mapper.writeValueAsString(getRequests);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
//
// 	}
//
// 	@Transactional
// 	@Test
// 	public void 유저_아이디와_튜터아이디_기본_조회테스트() {
//
// 		List<User> users = UserSetting();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(4), "api/ef/?");
// 		documentRepository.save(doc);
// 		List<FeedbackRequestDto> requestDtos = List.of(
// 			new FeedbackRequestDto(1L, 1L, "Text"),
// 			new FeedbackRequestDto(2L, 1L, "Text"),
// 			new FeedbackRequestDto(3L, 1L, "Text"),
// 			new FeedbackRequestDto(4L, 1L, "Text")
//
// 		);
// 		for(int  i =0; i <requestDtos.size()-1; i++){
// 			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
// 		}
//
// 		// RequestState requestState = RequestState.fromNumber(0);
//
// 		long start = System.currentTimeMillis();
// 		List<FeedbackRequestEntityResponseDto> getRequests =
// 		feedbackRequestService.getRequests(users.get(4).getUserId(), 2L, null, null, null, 0,20);
// 		long end= System.currentTimeMillis();
//
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(getRequests);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
//
// 	@Transactional
// 	@Test
// 	public void 유저_아이디와_문서아이디_기본_조회테스트() {
//
// 		List<User> users = UserSetting();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(4), "api/ef/?");
// 		documentRepository.save(doc);
// 		List<FeedbackRequestDto> requestDtos = List.of(
// 			new FeedbackRequestDto(1L, 1L, "Text"),
// 			new FeedbackRequestDto(2L, 1L, "Text"),
// 			new FeedbackRequestDto(3L, 1L, "Text"),
// 			new FeedbackRequestDto(4L, 1L, "Text")
//
// 		);
// 		for(int  i =0; i <requestDtos.size(); i++){
// 			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
// 		}
// 		RequestState requestState = RequestState.fromNumber(0);
//
// 		long start = System.currentTimeMillis();
// 		List<FeedbackRequestEntityResponseDto> getRequests =
// 		feedbackRequestService.getRequests(users.get(4).getUserId(), null, 1L, null, requestState, 0, 20);
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(getRequests);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
// 	@Transactional
// 	@Test
// 	public void 유저_아이디와_어느_조건_상관없이_Cancele제외_기본_조회테스트() {
//
// 		List<User> users = UserSetting();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(4), "api/ef/?");
// 		documentRepository.save(doc);
// 		FeedbackRequestDto canceledRequestDto = new FeedbackRequestDto(3L, 1L, "Text");
// 		FeedbackRequestEntity alreadyExistReuest = new FeedbackRequestEntity(canceledRequestDto, users.get(4), users.get(2),doc);
// 		alreadyExistReuest.updateRequestState(RequestState.CANCELED);
// 		feedbackRequestEntityRepository.save(alreadyExistReuest);
//
// 		List<FeedbackRequestDto> requestDtos = List.of(
// 			new FeedbackRequestDto(1L, 1L, "Text"),
// 			new FeedbackRequestDto(2L, 1L, "Text"),
// 			new FeedbackRequestDto(3L, 1L, "Text"),
// 			new FeedbackRequestDto(4L, 1L, "Text")
//
// 		);
//
// 		for(int  i =0; i <requestDtos.size(); i++){
// 			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
// 		}
// 		RequestState requestState = RequestState.fromNumber(0);
//
// 		long start = System.currentTimeMillis();
// 		List<FeedbackRequestEntityResponseDto> getRequests =
// 			feedbackRequestService.getRequests(users.get(4).getUserId(), null, 1L, null,requestState, 0, 20);
//
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(getRequests);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
//
// 	@Transactional
// 	@Test
// 	public void 유저_아이디와_날짜_조회테스트() {
//
// 		List<User> users = UserSetting();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(4), "api/ef/?");
// 		documentRepository.save(doc);
// 		FeedbackRequestDto canceledRequestDto = new FeedbackRequestDto(3L, 1L, "Text");
// 		FeedbackRequestEntity alreadyExistReuest = new FeedbackRequestEntity(canceledRequestDto, users.get(4), users.get(2),doc);
// 		alreadyExistReuest.updateRequestState(RequestState.CANCELED);
// 		feedbackRequestEntityRepository.save(alreadyExistReuest);
//
// 		List<FeedbackRequestDto> requestDtos = List.of(
// 			new FeedbackRequestDto(1L, 1L, "Text"),
// 			new FeedbackRequestDto(2L, 1L, "Text"),
// 			new FeedbackRequestDto(3L, 1L, "Text"),
// 			new FeedbackRequestDto(4L, 1L, "Text")
//
// 		);
//
// 		for(int  i =0; i <requestDtos.size(); i++){
// 			feedbackRequestService.createRequest(users.get(4).getUserId(), requestDtos.get(i));
// 		}
// 		RequestState requestState = RequestState.fromNumber(0);
// 		LocalDateTime month = null;
//
// 		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
// 		YearMonth ym = YearMonth.parse("2025-05", dateTimeFormatter);
// 		month = ym.atDay(1).atStartOfDay();  // LocalDateTime으로 변환
//
// 		long start = System.currentTimeMillis();
// 		List<FeedbackRequestEntityResponseDto> getRequests =
// 			feedbackRequestService.getRequests(users.get(4).getUserId(), null, 1L, month ,requestState, 0, 20);
//
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(getRequests);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
// 	@Transactional
// 	@Test
// 	public  void 요청_수정하기(){
// 		List<User> users =UserSetting ();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(4), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		FeedbackRequestDto requestDto = new FeedbackRequestDto(1L, 1L, "Text");
// 		feedbackRequestService.createRequest(users.get(4).getUserId(), requestDto);
//
// 		FeedbackRequestDto UpdateRequestDto = new FeedbackRequestDto(1L, 1L, "수정된 피드백 요청 내용");
// 		long start = System.currentTimeMillis();
// 		FeedbackRequestEntityResponseDto UpdateFeedbackRequestEntityResponseDto =
// 			feedbackRequestService.updateRequest( users.get(4).getUserId() ,1L, UpdateRequestDto);
//
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(UpdateFeedbackRequestEntityResponseDto);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
//
// 	@Transactional
// 	@Test
// 	public void 요청_취소하기(){
// 		List<User> users =UserSetting ();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(4), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		FeedbackRequestDto requestDto = new FeedbackRequestDto(1L, 1L, "Text");
// 		feedbackRequestService.createRequest(users.get(4).getUserId(), requestDto);
//
// 		long start = System.currentTimeMillis();
// 		FeedbackRequestEntityResponseDto feedbackRequestEntityResponseDto
// 			=  feedbackRequestService.cancelRequest(users.get(4).getUserId(),1L);
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(feedbackRequestEntityResponseDto);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
//
//
//
// 	public List<User> tutorSetting (){
// 		return List.of(
// 			new User("Astra1", "Test1@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			new User("Astra2", "Test2@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			new User("Astra3", "Test3@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			new User("Astra4", "Test4@naver.com", "tester1234", UserRole.APPROVED_TUTOR)
// 		);
// 	}
//
// 	public List<User> userSetting (){
// 		return List.of(
// 			new User("paragon", "Test30@naver.com", "tester1234", UserRole.STUDENT),
// 			new User("paragon1", "Test5@naver.com", "tester1234", UserRole.STUDENT),
// 			new User("paragon2", "Test6@naver.com", "tester1234", UserRole.STUDENT),
// 			new User("paragon3", "Test7@naver.com", "tester1234", UserRole.STUDENT),
// 			new User("paragon4", "Test8@naver.com", "tester1234", UserRole.STUDENT)
// 		);
// 	}
// 	@Transactional
// 	@Test
// 	public void 단건_상세조회_테스트(){
// 		List<User> tutors =tutorSetting();
// 		List<User> users =userSetting ();
// 		userRepository.saveAll(tutors );
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
// 		List<FeedbackRequestDto> requestDtos = List.of(
// 			new FeedbackRequestDto(tutors.get(0).getUserId(), 1L, "Text"),
// 			new FeedbackRequestDto(tutors.get(1).getUserId(), 1L, "Text"),
// 			new FeedbackRequestDto(tutors.get(2).getUserId(), 1L, "Text"),
// 			new FeedbackRequestDto(tutors.get(3).getUserId(), 1L, "Text")
//
// 		);
// 		for(int  i =0; i <requestDtos.size(); i++){
// 			feedbackRequestService.createRequest( users.get(1).getUserId(), requestDtos.get(i));
// 		}
// 		long start = System.currentTimeMillis();
// 		FeedbackResponseDetailsDto getRequest =  feedbackRequestService.getFeedbackRequest(tutors.get(1).getUserId(),1L);
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.registerModule(new JavaTimeModule());
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(getRequest);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
// 	@Transactional
// 	@Test
// 	public void 다건_조회_테스트(){
// 		List<User> tutors =tutorSetting();
// 		List<User> users =userSetting ();
// 		userRepository.saveAll(tutors );
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
// 		List<FeedbackRequestDto> requestDtos = List.of(
// 			new FeedbackRequestDto(1L, 1L, "Text"),
// 			new FeedbackRequestDto(2L, 1L, "Text"),
// 			new FeedbackRequestDto(3L, 1L, "Text"),
// 			new FeedbackRequestDto(4L, 1L, "Text")
//
// 		);
//
//
// 		feedbackRequestService.createRequest( users.get(0).getUserId(), requestDtos.get(3));
// 		feedbackRequestService.createRequest( users.get(1).getUserId(), requestDtos.get(3));
// 		feedbackRequestService.createRequest( users.get(2).getUserId(), requestDtos.get(3));
// 		feedbackRequestService.createRequest( users.get(3).getUserId(), requestDtos.get(3));
// 		feedbackRequestService.createRequest( users.get(4).getUserId(), requestDtos.get(3));
//
// 		long start = System.currentTimeMillis();
// 		List<FeedbackResponseDetailsDto> getRequests =  feedbackRequestService.getFeedbackRequests(tutors.get(3).getUserId(),0, 20);
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(getRequests);
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
// 	@Transactional
// 	@Test
// 	public void 피드백_거절(){
// 		List<User> tutors =tutorSetting();
// 		List<User> users =userSetting ();
// 		userRepository.saveAll(tutors );
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		//유저가 튜터4에게 피드백을 요청한다.
// 		FeedbackRequestDto testRequestDto = new FeedbackRequestDto(tutors.get(2).getUserId(), 1L, "Text");
// 		feedbackRequestService.createRequest( users.get(1).getUserId(), testRequestDto);
// 		//튜터는 특정 피드백 요청에 대해 거절 사유등록해서 거절을 승인한다.
//
// 		FeedbackRejectRequestDto feedbackRejectRequestDto =
// 			new FeedbackRejectRequestDto("OO 사유로 거절함.");
//
// 		long start = System.currentTimeMillis();
// 		FeedbackRequestEntityResponseDto response =
// 			feedbackRequestService .rejectFeedbackRequest(tutors.get(2).getUserId(),1L,5, feedbackRejectRequestDto);
// 		long end= System.currentTimeMillis();
// 		System.out.println("수정 작업 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(response); // 전체 객체를 JSON 변환
// 			System.out.println(json);
// 		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
//
//
// }
