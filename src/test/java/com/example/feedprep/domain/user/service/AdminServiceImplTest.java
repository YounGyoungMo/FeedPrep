package com.example.feedprep.domain.user.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.techstack.dto.CreateTechStackRequestDto;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.repository.TechStackRepository;
import com.example.feedprep.domain.user.dto.response.ApproveTutorResponseDto;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TechStackRepository techStackRepository;

    @InjectMocks
    private AdminServiceImpl adminServiceImpl;

    @Test
    @DisplayName("튜터 활동 승인")
    void approveTutor_success() {
        // given
        User user = User.builder().userId(1L).role(UserRole.PENDING_TUTOR).build();

        // when
        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);

        ApproveTutorResponseDto result = adminServiceImpl.approveTutor(1L);

        // then
        assertThat(result.getRole()).isEqualTo(UserRole.APPROVED_TUTOR);
    }

    @Test
    @DisplayName("승인 대기중인 튜터가 아닙니다")
    void approveTutor_NOT_PENDING_TUTOR() {
        // given
        User user = User.builder().userId(1L).role(UserRole.APPROVED_TUTOR).build();

        // when
        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);

        CustomException customException = assertThrows(CustomException.class, () -> {
            adminServiceImpl.approveTutor(1L);
        });

        // then
        assertThat(customException.getErrorCode())
            .isEqualTo(ErrorCode.NOT_PENDING_TUTOR);
    }

    @Test
    @DisplayName("기술스택 생성 성공")
    void createTechStack_success() {
        // given
        String techStackName = "기술스택";
        CreateTechStackRequestDto requestDto = new CreateTechStackRequestDto(techStackName);

        // when
        when(techStackRepository.findByTechStack(techStackName)).thenReturn(Optional.empty());

        adminServiceImpl.createTechStack(requestDto);

        // then
        ArgumentCaptor<TechStack> captor = ArgumentCaptor.forClass(TechStack.class);
        verify(techStackRepository).save(captor.capture());

        assertThat(captor.getValue().getTechStack()).isEqualTo(techStackName);
    }

    @Test
    @DisplayName("이미 등록된 기술스택 입니다")
    void createTechStack_ALREADY_REGISTERED_TECHSTACK() {
        // given
        String techStackName = "기술스택";
        CreateTechStackRequestDto requestDto = new CreateTechStackRequestDto(techStackName);
        TechStack techStack = TechStack.builder().techId(1L).techStack(techStackName).build();

        // when
        when(techStackRepository.findByTechStack(techStackName))
            .thenReturn(Optional.ofNullable(techStack));

        CustomException customException = assertThrows(CustomException.class, () -> {
            adminServiceImpl.createTechStack(requestDto);
        });

        // then
        assertThat(customException.getErrorCode())
            .isEqualTo(ErrorCode.ALREADY_REGISTERED_TECHSTACK);

    }

    @Test
    @DisplayName("기술스택 삭제 완료")
    void deleteTechStack_success() {
        // given
        TechStack techStack = TechStack.builder().techId(1L).techStack("기술스택").build();

        // when
        when(techStackRepository.findByIdOrElseThrow(1L)).thenReturn(techStack);

        adminServiceImpl.deleteTechStack(1L);

        // then
        verify(techStackRepository).delete(techStack);
    }

    @Test
    @DisplayName("기술 스택 정보를 찾을 수 없습니다")
    void deleteTechStack_TECH_STACK_NOT_FOUND() {
        // given

        // when
        when(techStackRepository.findByIdOrElseThrow(1L))
            .thenThrow(new CustomException(ErrorCode.TECH_STACK_NOT_FOUND));

        CustomException customException = assertThrows(CustomException.class, () -> {
            adminServiceImpl.deleteTechStack(1L);
        });

        // then
        assertThat(customException.getErrorCode())
            .isEqualTo(ErrorCode.TECH_STACK_NOT_FOUND);

    }
}