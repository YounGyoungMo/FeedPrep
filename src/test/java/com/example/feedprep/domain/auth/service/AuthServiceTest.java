 package com.example.feedprep.domain.auth.service;

 import com.example.feedprep.common.exception.base.CustomException;
 import com.example.feedprep.common.exception.enums.ErrorCode;
 import com.example.feedprep.domain.auth.dto.*;
 import com.example.feedprep.domain.auth.repository.RefreshTokenRepository;
 import com.example.feedprep.domain.user.entity.User;
 import com.example.feedprep.domain.user.enums.UserRole;
 import com.example.feedprep.domain.user.repository.UserRepository;
 import org.junit.jupiter.api.AfterEach;
 import org.junit.jupiter.api.Test;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.test.context.ActiveProfiles;

 import java.util.Set;

 import static org.assertj.core.api.Assertions.assertThat;
 import static org.assertj.core.api.Assertions.assertThatCode;
 import static org.junit.jupiter.api.Assertions.assertThrows;

 @SpringBootTest
 @ActiveProfiles("test")
 public class AuthServiceTest {

     @Value("${jwt.secret.key}")
     private String secretCode;

     @Autowired
     private AuthService authService;

     @Autowired
     private UserRepository userRepository;

     @Autowired
     private RefreshTokenRepository refreshTokenRepository;

     @Autowired
     private PasswordEncoder passwordEncoder;

     @AfterEach
     void cleanUp() {
         refreshTokenRepository.deleteAll();
         userRepository.deleteAll();
     }

     @Test
     void 성공_회원가입_학생() {
         // given
         SignupRequestDto request = new SignupRequestDto("name1", "email1@example.com", "passworDd12", "STUDENT");

         // when
         SignupResponseDto response = authService.signup(request);

         // then
         // DB에 실제로 저장됐는지 확인
         User user = userRepository.findByEmail("email1@example.com").orElseThrow();
         assertThat(user).isNotNull();
         assertThat(user.getEmail()).isEqualTo("email1@example.com");
         assertThat(user.getName()).isEqualTo("name1");
         assertThat(user.getRole()).isEqualTo(UserRole.STUDENT);

         // 올바른 응답값인지 확인
         assertThat(response.getEmail()).isEqualTo("email1@example.com");
         assertThat(response.getName()).isEqualTo("name1");
         assertThat(response.getRole()).isEqualTo(UserRole.STUDENT);

         // 인코딩된 비밀번호가 저장되었는지 확인
         assertThat(passwordEncoder.matches("passworDd12", user.getPassword())).isTrue();
     }

     @Test
     void 성공_회원가입_튜터() {
         // given - 튜터 회원 가입 요청 DTO 생성
         SignupRequestDto request = new SignupRequestDto("name1", "email1@example.com", "passworDd12", "TUTOR");

         // when - 회원 가입 시도
         SignupResponseDto response = authService.signup(request);

         // then - 회원 가입 및 응답 검증
         // DB에 저장된 사용자 정보 확인
         User user = userRepository.findByEmail("email1@example.com").orElseThrow();
         assertThat(user).isNotNull();
         assertThat(user.getEmail()).isEqualTo("email1@example.com");
         assertThat(user.getName()).isEqualTo("name1");
         assertThat(user.getRole()).isEqualTo(UserRole.PENDING_TUTOR);

         // 응답 DTO 값 확인
         assertThat(response.getEmail()).isEqualTo("email1@example.com");
         assertThat(response.getName()).isEqualTo("name1");
         assertThat(response.getRole()).isEqualTo(UserRole.PENDING_TUTOR);

         // 인코딩된 비밀번호가 저장되었는지 확인
         assertThat(passwordEncoder.matches("passworDd12", user.getPassword())).isTrue();

     }

     @Test
     void 성공_회원가입_관리자() {
         // given
         AdminSignupRequestDto request = new AdminSignupRequestDto("name1", "email1@example.com", "passworDd12", "ADMIN", secretCode);

         // when
         SignupResponseDto response = authService.adminSignup(request);

         // then
         // DB에 실제로 저장됐는지 확인
         User user = userRepository.findByEmail("email1@example.com").orElseThrow();
         assertThat(user).isNotNull();
         assertThat(user.getEmail()).isEqualTo("email1@example.com");
         assertThat(user.getName()).isEqualTo("name1");
         assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);

         // 올바른 응답값인지 확인
         assertThat(response.getEmail()).isEqualTo("email1@example.com");
         assertThat(response.getName()).isEqualTo("name1");
         assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);

         // 인코딩된 비밀번호가 저장되었는지 확인
         assertThat(passwordEncoder.matches("passworDd12", user.getPassword())).isTrue();
     }

     @Test
     void 실패_중복회원가입() {
         // given - 회원 가입
         SignupRequestDto request = new SignupRequestDto("name1", "email1@example.com", "passworDd12", "STUDENT");
         authService.signup(request);

         // when - 같은 이메일로 재가입 시도
         SignupRequestDto overlappingRequest = new SignupRequestDto("name1", "email1@example.com", "passworDd12", "STUDENT");
         CustomException exception = assertThrows(CustomException.class, () -> {
             authService.signup(overlappingRequest);
         });

         // then - 중복으로 인한 예외가 발생하는지 검증
         assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
     }

     @Test
     void 성공_로그인() {
         // given - 회원 가입
         SignupRequestDto request = new SignupRequestDto("name1", "email1@example.com", "passworDd12", "STUDENT");
         authService.signup(request);

         // when - 로그인 시도
         LoginRequestDto loginRequest = new LoginRequestDto("email1@example.com", "passworDd12");
         TokenResponseDto tokenResponse = authService.login(loginRequest, Set.of("STUDENT", "PENDING_TUTOR"));

         // then - 토큰 발급 검증
         assertThat(tokenResponse).isNotNull();
         assertThat(tokenResponse.getAccessToken()).isNotBlank();
         assertThat(tokenResponse.getRefreshToken()).isNotBlank();
     }

     @Test
     void 실패_로그인_잘못된_이메일_혹은_비밀번호() {
         // given - 회원 가입
         SignupRequestDto request = new SignupRequestDto("name1", "email1@example.com", "passworDd12", "STUDENT");
         authService.signup(request);

         // when - 로그인 시도
         LoginRequestDto loginRequest = new LoginRequestDto("email1@example.com", "wrong_password");

         // then - 인증 실패시 발생하는 예외 검증
         CustomException exception = assertThrows(CustomException.class, () -> {
             authService.login(loginRequest, Set.of("STUDENT"));
         });

         assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
     }

     @Test
     void 성공_로그아웃() {
         // given - 회원 가입
         SignupRequestDto request = new SignupRequestDto("name1", "email1@example.com", "passworDd12", "STUDENT");
         authService.signup(request);

         // when - 로그인 시도
         LoginRequestDto loginRequest = new LoginRequestDto("email1@example.com", "passworDd12");
         TokenResponseDto tokenResponse = authService.login(loginRequest, Set.of("STUDENT"));

         String accessToken = tokenResponse.getAccessToken();
         String bearerToken = "Bearer " + accessToken;
         User user = userRepository.getUserByEmailOrElseThrow("email1@example.com");
         Long userId = user.getUserId();

         // then - 로그아웃 시 어떤 예외도 발생하지 않고 작동하는지 검증
         assertThatCode(() -> authService.logout(bearerToken, userId)).doesNotThrowAnyException();
     }

     @Test
     void 성공_회원_탈퇴() {
         // given - 회원 가입 및 로그인
         SignupRequestDto request = new SignupRequestDto("name1", "email1@example.com", "passworDd12", "STUDENT");
         authService.signup(request);

         LoginRequestDto loginRequest = new LoginRequestDto("email1@example.com", "passworDd12");
         TokenResponseDto tokenResponse = authService.login(loginRequest, Set.of("STUDENT"));

         String accessToken = tokenResponse.getAccessToken();
         String bearerToken = "Bearer " + accessToken;
         User user = userRepository.getUserByEmailOrElseThrow("email1@example.com");
         Long userId = user.getUserId();

         // when & then - 탈퇴 시도
         assertThatCode(()-> authService.withdraw(bearerToken, userId)).doesNotThrowAnyException();
     }

 }
