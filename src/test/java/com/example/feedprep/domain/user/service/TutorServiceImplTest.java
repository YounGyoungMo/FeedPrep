package com.example.feedprep.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.message.service.TutorMessageService;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.notification.service.NotificationService;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
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
class TutorServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TutorMessageService tutorMessageService;

    @InjectMocks
    private TutorServiceImpl tutorService;

    @Test
    @DisplayName("튜터 활동 신청 보내기")
    void sendRequestActivity_SUCCESS() {

        User tutor = User.builder().userId(1L).role(UserRole.PENDING_TUTOR).build();
        User admin = User.builder().userId(2L).role(UserRole.ADMIN).build();
        Document document = Document.builder().documentId(3L).fileUrl("test-url").build();

        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(tutor);
        when(userRepository.findByIdOrElseThrow(2L)).thenReturn(admin);
        when(documentRepository.findByIdOrElseThrow(3L)).thenReturn(document);

        tutorService.sendRequestActivity(tutor.getUserId(),admin.getUserId(),document.getDocumentId());

        verify(tutorMessageService, times(1))
            .createdMessageTutor(tutor.getUserId(), document.getDocumentId(), document.getFileUrl());
        verify(notificationService, times(1))
            .sendNotification(tutor,admin,201);
    }

    @Test
    @DisplayName("활동 대기 중인 튜터가 아님")
    void sendRequestActivity_NOT_PENDING_TUTOR() {

        User notPendingTutor = User.builder().userId(1L).role(UserRole.APPROVED_TUTOR).build();
        User admin = User.builder().userId(2L).role(UserRole.ADMIN).build();
        Document document = Document.builder().documentId(3L).fileUrl("test-url").build();

        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(notPendingTutor);

        CustomException customException = assertThrows(CustomException.class, () -> {
            tutorService.sendRequestActivity(notPendingTutor.getUserId(),admin.getUserId(),document.getDocumentId());
        });

        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_PENDING_TUTOR);

    }

    @Test
    @DisplayName("관리자가 아님")
    void sendRequestActivity_NOT_EQUALS_ADMIN() {

        User tutor = User.builder().userId(1L).role(UserRole.PENDING_TUTOR).build();
        User notAdmin = User.builder().userId(2L).role(UserRole.APPROVED_TUTOR).build();
        Document document = Document.builder().documentId(3L).fileUrl("test-url").build();

        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(tutor);
        when(userRepository.findByIdOrElseThrow(2L)).thenReturn(notAdmin);

        CustomException customException = assertThrows(CustomException.class, () -> {
            tutorService.sendRequestActivity(tutor.getUserId(),notAdmin.getUserId(),document.getDocumentId());
        });

        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_EQUALS_ADMIN);
    }

    @Test
    @DisplayName("보낸 신청 목록 보기")
    void getRequestList_SUCCESS() {
        Long userId = 1L;

        tutorService.getRequestList(userId);

        verify(tutorMessageService, times(1)).getMessageTutorList(userId);
    }
}
