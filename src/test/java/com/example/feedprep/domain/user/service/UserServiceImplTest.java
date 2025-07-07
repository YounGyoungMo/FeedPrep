package com.example.feedprep.domain.user.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.techstack.dto.UserTechStackDto;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.user.dto.request.NewPasswordRequestDto;
import com.example.feedprep.domain.user.dto.request.UpdateMyInfoRequestDto;
import com.example.feedprep.domain.user.dto.response.PasswordModifiedAtResponseDto;
import com.example.feedprep.domain.user.dto.response.TutorResponseDto;
import com.example.feedprep.domain.user.dto.response.UserResponseDto;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    @DisplayName("튜터 목록 조회 성공")
    void getTutorList_success() {
        // given
        UserTechStack uts1 = UserTechStack.builder().relationId(11L)
            .techStack(TechStack.builder().techStack("Java").build()).build();
        UserTechStack uts2 = UserTechStack.builder().relationId(12L)
            .techStack(TechStack.builder().techStack("Spring").build()).build();

        User user1 = User.builder().userId(1L).role(UserRole.APPROVED_TUTOR)
            .userTechStacks(List.of(uts1)).build();
        User user2 = User.builder().userId(2L).role(UserRole.APPROVED_TUTOR)
            .userTechStacks(List.of(uts2)).build();

        List<User> mockUserList = Arrays.asList(user1,user2);

        // when
        when(userRepository.findAllByRole(UserRole.APPROVED_TUTOR)).thenReturn(mockUserList);

        List<TutorResponseDto> result = userServiceImpl.getTutorList();

        // then
        TutorResponseDto tutor1 = new TutorResponseDto(
            user1.getUserId(),
            null,
            null,
            user1.getRole(),
            user1.getRating(),
            List.of(new UserTechStackDto(uts1.getRelationId(), uts1.getTechStack().getTechStack())));
        TutorResponseDto tutor2 = new TutorResponseDto(
            user2.getUserId(),
            null,
            null,
            user2.getRole(),
            user2.getRating(),
            List.of(new UserTechStackDto(uts2.getRelationId(), uts2.getTechStack().getTechStack())));

        assertThat(result)
            .isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrder(tutor1,tutor2)
            .extracting("role")
            .allMatch(role -> role == UserRole.APPROVED_TUTOR);
    }

    @Test
    @DisplayName("튜터 목록 조회 실패 - 튜터 없음")
    void getTutorList_fail() {
        // given
        List<User> mockUserList = List.of();

        // when
        when(userRepository.findAllByRole(UserRole.APPROVED_TUTOR)).thenReturn(mockUserList);
        
        // then
        CustomException customException = assertThrows(CustomException.class, () -> {
            userServiceImpl.getTutorList();
        });

        assertThat(customException.getErrorCode())
            .isEqualTo(ErrorCode.NOT_FOUND_TUTOR);
    }

    @Test
    @DisplayName("내 정보 조회하기")
    void getMyInfo() {
        // given
        User user = User.builder().userId(1L).name("userName").email("userEmail").role(UserRole.STUDENT).build();

        // when
        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);

        UserResponseDto result = userServiceImpl.getMyInfo(1L);

        // then
        UserResponseDto userResponseDto = new UserResponseDto(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getAddress(),
            user.getIntroduction(),
            user.getRole(),
            user.getRating(),
            user.getUserTechStacks(),
            null,
            null
        );

        assertThat(result)
            .isNotNull()
            .isEqualTo(userResponseDto);
    }

    @Test
    @DisplayName("내 정보 수정하기")
    void updateMyInfo() {
        // given
        User user = User.builder().userId(1L).name("oldName").address("oldAddress")
            .introduction("oldIntroduction").build();

        UpdateMyInfoRequestDto updateMyInfoRequestDto = new UpdateMyInfoRequestDto(
            null, "newAddress","newIntroduction");

        // when
        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);

        UserResponseDto result = userServiceImpl.updateMyInfo(1L, updateMyInfoRequestDto);

        // then
        UserResponseDto userResponseDto = new UserResponseDto(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            updateMyInfoRequestDto.getAddress(),
            updateMyInfoRequestDto.getIntroduction(),
            user.getRole(),
            user.getRating(),
            user.getUserTechStacks(),
            user.getCreatedAt(),
            user.getModifiedAt()
        );

        assertThat(result)
            .isNotNull()
            .isEqualTo(userResponseDto);
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_success() {
        //given
        String oldPassword = passwordEncoder.encode("oldPassword");
        String newPassword = passwordEncoder.encode("newPassword");

        User userSpy = Mockito.spy(
            User.builder()
                .userId(1L)
                .password(oldPassword)
                .build()
        );

        NewPasswordRequestDto newPasswordRequestDto =
            new NewPasswordRequestDto("oldPassword","newPassword");

        // when
        doReturn(userSpy).when(userRepository).findByIdOrElseThrow(1L);
        when(passwordEncoder.matches(newPasswordRequestDto.getOldPassword(), userSpy.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPasswordRequestDto.getNewPassword())).thenReturn(newPassword);
        LocalDateTime modifiedAt = LocalDateTime.now();
        doReturn(modifiedAt).when(userSpy).getModifiedAt();

        PasswordModifiedAtResponseDto result = userServiceImpl.changePassword(
            1L, newPasswordRequestDto);

        //then
        assertThat(userSpy.getPassword()).isEqualTo(newPassword);
        assertThat(result.getModifiedAt()).isCloseTo(LocalDateTime.now(),
            within(5, ChronoUnit.SECONDS));
    }

    @Test
    @DisplayName("비밀번호 변경 실패")
    void changePassword_fail() {
        //given
        String oldPassword = passwordEncoder.encode("oldPassword");

        User userSpy = Mockito.spy(
            User.builder()
                .userId(1L)
                .password(oldPassword)
                .build()
        );

        NewPasswordRequestDto newPasswordRequestDto =
            new NewPasswordRequestDto("otherPassword","newPassword");

        // when
        doReturn(userSpy).when(userRepository).findByIdOrElseThrow(1L);
        when(passwordEncoder.matches(newPasswordRequestDto.getOldPassword(), userSpy.getPassword())).thenReturn(false);

        //then
        CustomException customException = assertThrows(CustomException.class, () -> {
            userServiceImpl.changePassword(1L, newPasswordRequestDto);
        });

        assertThat(customException.getErrorCode())
            .isEqualTo(ErrorCode.NOT_MATCH_PASSWORD);
    }
}