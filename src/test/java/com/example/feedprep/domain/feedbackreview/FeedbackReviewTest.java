// package com.example.feedprep.domain.feedbackreview;
//
// import java.util.List;
// import java.util.Random;
// import java.util.stream.IntStream;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;
// import com.example.feedprep.common.response.ApiResponseDto;
// import com.example.feedprep.domain.document.entity.Document;
// import com.example.feedprep.domain.document.repository.DocumentRepository;
// import com.example.feedprep.domain.feedback.dto.request.FeedbackWriteRequestDto;
// import com.example.feedprep.domain.feedback.service.FeedbackService;
// import com.example.feedprep.domain.feedbackrequestentity.dto.request.FeedbackRequestDto;
// import com.example.feedprep.domain.feedbackrequestentity.repository.FeedbackRequestEntityRepository;
// import com.example.feedprep.domain.feedbackrequestentity.service.FeedbackRequestService;
// import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewRequestDto;
// import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewResponseDto;
// import com.example.feedprep.domain.feedbackreview.service.FeedbackReviewService;
// import com.example.feedprep.domain.user.entity.User;
// import com.example.feedprep.domain.user.enums.UserRole;
// import com.example.feedprep.domain.user.repository.UserRepository;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;
//
// @ActiveProfiles("local")
// @SpringBootTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// public class FeedbackReviewTest {
// 	@Autowired
// 	private FeedbackRequestEntityRepository feedbackRequestEntityRepository;
// 	@Autowired
// 	private UserRepository userRepository;
// 	@Autowired
// 	private DocumentRepository documentRepository;
// 	@Autowired
// 	private FeedbackRequestService feedbackRequestService;
// 	@Autowired
// 	private FeedbackService feedbackService;
// 	@Autowired
// 	private FeedbackReviewService feedbackReviewService;
//
//
// 	public List<User> userSetting (){
// 		return List.of(
// 			new User("Astra1", "Test1@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			new User("paragon", "Test30@naver.com", "tester1234", UserRole.STUDENT),
// 			new User("paragon1", "Test5@naver.com", "tester1234", UserRole.STUDENT),
// 			new User("paragon2", "Test6@naver.com", "tester1234", UserRole.STUDENT),
// 			new User("paragon3", "Test7@naver.com", "tester1234", UserRole.STUDENT),
// 			new User("paragon4", "Test8@naver.com", "tester1234", UserRole.STUDENT)
// 		);
// 	}
// 	@Transactional
// 	@Test
// 	public void 리뷰_생성_테스트(){
//
//        	List<User> users =userSetting();
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		//한 튜터에 대한 피드백 요청 신청
// 		FeedbackRequestDto requestDto
// 			= new FeedbackRequestDto(users.get(0).getUserId(), doc.getDocumentId(), "Text");
//
// 		feedbackRequestService.createRequest(users.get(1).getUserId(), requestDto);
//
// 		//피드백 요청에 대한 피드백 작성
// 		FeedbackWriteRequestDto feedbackWriteRequestDto
// 			= new FeedbackWriteRequestDto("날먹잼");
//
// 		feedbackService.createFeedback(users.get(0).getUserId(), 1L, feedbackWriteRequestDto);
//
// 		//리뷰 작성 완료하기.
//
// 		FeedbackReviewRequestDto feedbackReviewRequestDto
// 			=new FeedbackReviewRequestDto(5, "튜터님 좋은 조언 감사합니다." );
//
// 		long start = System.currentTimeMillis();
// 		FeedbackReviewResponseDto feedbackReviewResponseDto =
// 			feedbackReviewService. createReview( users.get(1).getUserId(),1L , feedbackReviewRequestDto);
// 		long end= System.currentTimeMillis();
// 		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString( feedbackReviewResponseDto); // 전체 객체를 JSON 변환
// 			System.out.println(json);
// 		} catch (JsonProcessingException e) {
// 			e.printStackTrace();
// 		}
// 	}
//
//
// 	@Transactional
// 	@Test
// 	public void 리뷰_수정_테스트(){
// 		List<User> users =userSetting();
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		//한 튜터에 대한 피드백 요청 신청
// 		FeedbackRequestDto requestDto
// 			= new FeedbackRequestDto(users.get(0).getUserId(), doc.getDocumentId(), "Text");
//
// 		feedbackRequestService.createRequest(users.get(1).getUserId(), requestDto);
//
// 		//피드백 요청에 대한 피드백 작성
// 		FeedbackWriteRequestDto feedbackWriteRequestDto
// 			= new FeedbackWriteRequestDto("날먹잼");
//
// 		feedbackService.createFeedback(users.get(0).getUserId(), 1L, feedbackWriteRequestDto);
// 		//리뷰 작성 완료하기.
//
// 		FeedbackReviewRequestDto feedbackReviewRequestDto
// 			=new FeedbackReviewRequestDto( 5, "같이 좀..." );
//
// 		feedbackReviewService.createReview(users.get(0).getUserId(),1L , feedbackReviewRequestDto);
//
//
// 		//리뷰 수정하기
// 		FeedbackReviewRequestDto feedbackReviewUpdateRequestDto
// 			=new FeedbackReviewRequestDto( 5, "정직하게 리뷰 하겠습니다." );
// 		long start = System.currentTimeMillis();
// 		FeedbackReviewResponseDto feedbackReviewUpdateResponseDto =
// 			feedbackReviewService.updateReview( users.get(0).getUserId(),1L , feedbackReviewUpdateRequestDto);
// 		long end= System.currentTimeMillis();
// 		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(  feedbackReviewUpdateResponseDto); // 전체 객체를 JSON 변환
// 			System.out.println(json);
// 		} catch (JsonProcessingException e) {
// 			e.printStackTrace();
// 		}
// 	}
// 	@Transactional
// 	@Test
// 	public void 리뷰_삭제_테스트(){
// 		List<User> users =userSetting();
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		//한 튜터에 대한 피드백 요청 신청
// 		FeedbackRequestDto requestDto
// 			= new FeedbackRequestDto(users.get(0).getUserId(), doc.getDocumentId(), "Text");
//
// 		feedbackRequestService.createRequest(users.get(1).getUserId(), requestDto);
//
// 		//피드백 요청에 대한 피드백 작성
// 		FeedbackWriteRequestDto feedbackWriteRequestDto
// 			= new FeedbackWriteRequestDto("날먹잼");
//
// 		feedbackService.createFeedback(users.get(0).getUserId(), 1L, feedbackWriteRequestDto);
// 		//리뷰 작성 완료하기.
//
// 		FeedbackReviewRequestDto feedbackReviewRequestDto
// 			=new FeedbackReviewRequestDto( 5, "같이 좀..." );
//
// 		feedbackReviewService.createReview( users.get(0).getUserId(),1L , feedbackReviewRequestDto);
//
// 		//리뷰 삭제하기
//
// 		long start = System.currentTimeMillis();
// 		ApiResponseDto FeedbackReviewDeleteResponse =
// 			feedbackReviewService.deleteReview(users.get(0).getUserId(),1L);
// 		long end= System.currentTimeMillis();
// 		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(FeedbackReviewDeleteResponse); // 전체 객체를 JSON 변환
// 			System.out.println(json);
// 		} catch (JsonProcessingException e) {
// 			e.printStackTrace();
// 		}
// 	}
// 	@Transactional
// 	@Test
// 	public void 리뷰_유저_단건_조회_테스트(){
// 		List<User> users =userSetting();
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		//한 튜터에 대한 피드백 요청 신청
// 		FeedbackRequestDto requestDto
// 			= new FeedbackRequestDto(users.get(0).getUserId(), doc.getDocumentId(), "Text");
//
// 		feedbackRequestService.createRequest(users.get(1).getUserId(), requestDto);
//
// 		//피드백 요청에 대한 피드백 작성
// 		FeedbackWriteRequestDto feedbackWriteRequestDto
// 			= new FeedbackWriteRequestDto("날먹잼");
//
// 		feedbackService.createFeedback(users.get(0).getUserId(), 1L, feedbackWriteRequestDto);
// 		//리뷰 작성 완료하기.
//
// 		FeedbackReviewRequestDto feedbackReviewRequestDto
// 			=new FeedbackReviewRequestDto( 5, "좋은 피드백이었습니다." );
//
// 		feedbackReviewService.createReview( users.get(1).getUserId(),1L , feedbackReviewRequestDto);
// 		//리뷰 조회하기
//
// 		long start = System.currentTimeMillis();
// 		FeedbackReviewResponseDto feedbackReviewResponse
// 			=  feedbackReviewService.getReview(users.get(1).getUserId() , 1L);
// 		long end= System.currentTimeMillis();
// 		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(feedbackReviewResponse); // 전체 객체를 JSON 변환
// 			System.out.println(json);
// 		} catch (JsonProcessingException e) {
// 			e.printStackTrace();
// 		}
// 	}
// 	@Transactional
// 	@Test
// 	public void 리뷰__다건_조회_유저_기준_테스트(){
// 		List<User> users =userSetting();
// 		userRepository.saveAll(users );
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
//
//          List<User> tutors = List.of(
// 			 new User("아카시아", "Test65@naver.com", "tester1234", UserRole.APPROVED_TUTOR),
// 			 new User("아카시아2", "Test66@naver.com", "tester1234", UserRole.APPROVED_TUTOR)
// 		 ) ;
// 		userRepository.save( tutors.get(0) );
// 		userRepository.save( tutors.get(1) );
//
//
// 		//여러 튜터에게 피드백 요청 신청
// 		FeedbackRequestDto requestDtoTutor1 = new FeedbackRequestDto(users.get(0).getUserId(), doc.getDocumentId(), "Text");
// 		FeedbackRequestDto requestDtoTutor2 = new FeedbackRequestDto(tutors.get(0).getUserId(), doc.getDocumentId(), "Text");
// 		FeedbackRequestDto requestDtoTutor3 = new FeedbackRequestDto(tutors.get(1).getUserId(), doc.getDocumentId(), "Text");
//
//
// 		feedbackRequestService.createRequest(users.get(2).getUserId(), requestDtoTutor1);
// 		feedbackRequestService.createRequest(users.get(2).getUserId(), requestDtoTutor2);
// 		feedbackRequestService.createRequest(users.get(2).getUserId(), requestDtoTutor3);
//
// 		List<FeedbackWriteRequestDto> testFedbackWriteRequestDtos = List.of(
// 			new FeedbackWriteRequestDto( "~이런점을 수정하면 좋을것 같습니다."),
// 			new FeedbackWriteRequestDto( "~이런점을 수정하면 좋을것 같습니다."),
// 			new FeedbackWriteRequestDto( "~이런점을 수정하면 좋을것 같습니다.")
// 		);
// 		feedbackService.createFeedback(users.get(0).getUserId(), 1L, testFedbackWriteRequestDtos.get(0));
// 		feedbackService.createFeedback(tutors.get(0).getUserId(), 2L, testFedbackWriteRequestDtos.get(1));
// 		feedbackService.createFeedback(tutors.get(1).getUserId(), 3L, testFedbackWriteRequestDtos.get(2));
// 		System.out.println("신청완료");
//
// 		//리뷰 작성
//
// 		//리뷰 작성 완료하기.
//
// 		for(Long i = 1L; i <4L; i++) {
// 			FeedbackReviewRequestDto feedbackReviewRequestDto
// 				= new FeedbackReviewRequestDto(5, "좋은 피드백이었습니다.");
//
// 			feedbackReviewService.createReview(users.get(2).getUserId(), i , feedbackReviewRequestDto);
//
// 		}
// 		System.out.println("리뷰 작성");
// 		//리뷰 조회하기
// 		long start = System.currentTimeMillis();
// 		List<FeedbackReviewResponseDto> feedbackReviewResponse
// 			= feedbackReviewService.getReviews(users.get(2).getUserId(), 0, 10);
// 		long end = System.currentTimeMillis();
// 		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(feedbackReviewResponse); // 전체 객체를 JSON 변환
// 			System.out.println(json);
// 		} catch (JsonProcessingException e) {
// 			e.printStackTrace();
// 		}
// 	}
//
// 	@Transactional
// 	@Test
// 	public void 리뷰_튜터_다건_조회_테스트() {
//
// 		List<User> users = userSetting();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		//한 튜터에게 여러명이 다른 피드백 요청 신청
// 		FeedbackRequestDto requestDto = new FeedbackRequestDto(users.get(0).getUserId(), doc.getDocumentId(), "Text");
//
// 		IntStream.range(1, users.size())
// 			.forEach(i -> feedbackRequestService.createRequest(users.get(i).getUserId(), requestDto));
// 		System.out.println("신청완료");
//
// 		//피드백 요청에 대한 피드백 작성
// 		for (Long i = 0L; i < users.size()-1; i++) {
// 			FeedbackWriteRequestDto testfeedbackWriteRequestDto =
// 				new FeedbackWriteRequestDto( "~이런점을 수정하면 좋을것 같습니다.");
//
// 			feedbackService.createFeedback(users.get(0).getUserId(), i + 1, testfeedbackWriteRequestDto);
// 		}
// 		System.out.println("리뷰 작성");
// 		//리뷰 작성 완료하기.
//
// 		int sid = 1;
// 		for (Long i = 1L; i < users.size(); i++) {
// 			FeedbackReviewRequestDto feedbackReviewRequestDto
// 				= new FeedbackReviewRequestDto( 5, "좋은 피드백이었습니다.");
//
// 			feedbackReviewService.createReview( users.get(sid).getUserId(), i, feedbackReviewRequestDto);
// 			sid++;
// 		}
//
// 		//리뷰 조회하기
// 		long start = System.currentTimeMillis();
// 		List<FeedbackReviewResponseDto> feedbackReviewResponse
// 			= feedbackReviewService.getReviews(users.get(0).getUserId(), 0, 10);
// 		long end = System.currentTimeMillis();
// 		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(feedbackReviewResponse); // 전체 객체를 JSON 변환
// 			System.out.println(json);
// 		} catch (JsonProcessingException e) {
// 			e.printStackTrace();
// 		}
// 	}
//
// 	@Transactional
// 	@Test
// 	public void 리뷰_평균_조회_테스트(){
// 		List<User> users = userSetting();
// 		userRepository.saveAll(users);
//
// 		Document doc = new Document(users.get(1), "api/ef/?");
// 		documentRepository.save(doc);
//
// 		//한 튜터에게 여러명이 다른 피드백 요청 신청
// 		FeedbackRequestDto requestDto = new FeedbackRequestDto(users.get(0).getUserId(), doc.getDocumentId(), "Text");
//
// 		IntStream.range(1, users.size())
// 			.forEach(i -> feedbackRequestService.createRequest(users.get(i).getUserId(), requestDto));
// 		System.out.println("신청완료");
//
// 		//피드백 요청에 대한 피드백 작성
// 		for(Long i = 1L; i < users.size(); i++) {
// 			FeedbackWriteRequestDto testfeedbackWriteRequestDto =
// 				new FeedbackWriteRequestDto( "~이런점을 수정하면 좋을것 같습니다.");
//
// 			feedbackService.createFeedback(users.get(0).getUserId(), i, testfeedbackWriteRequestDto);
// 		}
// 		System.out.println("리뷰 작성");
// 		Random random = new Random();
//
// 		//리뷰 작성 완료하기.
// 		int student =1;
// 		int rat = 0;
// 		for(Long i = 1L; i < users.size(); i++) {
// 			rat = random.nextInt(0,5);
// 			FeedbackReviewRequestDto feedbackReviewRequestDto
// 				= new FeedbackReviewRequestDto(rat, "좋은 피드백이었습니다.");
//
// 			feedbackReviewService.createReview( users.get(student).getUserId(), i ,feedbackReviewRequestDto);
// 			student++;
// 			if(student == users.size()){
// 				break;
// 			}
// 		}
// 		//리뷰 조회하기
// 		List<FeedbackReviewResponseDto> feedbackReviewResponse=
// 			feedbackReviewService.getReviews(users.get(0).getUserId(), 0,10);
//
// 		long start = System.currentTimeMillis();
//
// 		double Rating = feedbackReviewService.getAverageRating(users.get(0).getUserId());
//
// 		long end= System.currentTimeMillis();
// 		System.out.println("첫 실행 시간: " + (end - start) + "ms"); // DB 조회
//
// 		ObjectMapper mapper = new ObjectMapper();
// 		mapper.enable(SerializationFeature.INDENT_OUTPUT); // 예쁘게 출력
//
// 		try {
// 			String json = mapper.writeValueAsString(feedbackReviewResponse); // 전체 객체를 JSON 변환
// 			System.out.println(json);
// 		} catch (JsonProcessingException e) {
// 			e.printStackTrace();
// 		}
// 		System.out.println(Rating);
// 	}
// }
