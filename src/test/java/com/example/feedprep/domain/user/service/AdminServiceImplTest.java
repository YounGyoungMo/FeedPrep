package com.example.feedprep.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.feedprep.domain.techstack.repository.TechStackRepository;
import com.example.feedprep.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
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

        // when

        // then

    }

    @Test
    @DisplayName("승인 대기중인 튜터가 아닙니다")
    void approveTutor_NOT_PENDING_TUTOR() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("기술스택 생성 성공")
    void createTechStack_success() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("이미 등록된 기술스택 입니다")
    void createTechStack_ALREADY_REGISTERED_TECHSTACK() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("기술스택 삭제 완료")
    void deleteTechStack_success() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("기술 스택 정보를 찾을 수 없습니다")
    void deleteTechStack_TECH_STACK_NOT_FOUND() {
        // given

        // when

        // then

    }
}