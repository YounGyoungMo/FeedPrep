package com.example.feedprep.common.message.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import com.example.feedprep.common.message.entity.TutorMessage;
import com.example.feedprep.common.message.repository.MessageRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TutorMessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private TutorMessageServiceImpl tutorMessageService;

    @Test
    @DisplayName("튜터 활동 신청 메세지를 저장한다.")
    void createdMessageTutor() {
        Long tutorId = 1L;
        Long documentId = 2L;
        String fileUrl = "test-url";

        tutorMessageService.createdMessageTutor(tutorId, documentId, fileUrl);

        ArgumentCaptor<TutorMessage> captor = ArgumentCaptor.forClass(TutorMessage.class);

        verify(messageRepository, times(1)).save(captor.capture());

        TutorMessage result = captor.getValue();
        assertThat(result)
            .extracting(
                TutorMessage::getUserId,
                TutorMessage::getDocumentId,
                TutorMessage::getFileName
            )
            .contains(1L, 2L, "test-url");
    }

    @Test
    @DisplayName("받은 튜터 활동 신청 메시지 단건을 조회 한다.")
    void getMessageTutor() {
        TutorMessage tutorMessage = TutorMessage.builder().messageId(1L).build();

        when(messageRepository.findByMessageIdOrElseThrow(1L)).thenReturn(tutorMessage);

        TutorMessageResponseDdo messageTutor = tutorMessageService.getMessageTutor(1L);

        assertThat(messageTutor).isNotNull();
        assertThat(messageTutor.getMessageId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("신청한 튜터의 모든 메세지를 조회 한다.")
    void getMessageTutorList_SECCESS() {
        TutorMessage tutorMessage1 = TutorMessage.builder().messageId(1L).userId(1L).build();
        TutorMessage tutorMessage2 = TutorMessage.builder().messageId(2L).userId(1L).build();

        List<TutorMessage> mockMessageList = Arrays.asList(tutorMessage1,tutorMessage2);

        when(messageRepository.findAllByUserId(1L)).thenReturn(mockMessageList);

        List<TutorMessageResponseDdo> result = tutorMessageService.getMessageTutorList(
            1L);

        TutorMessageResponseDdo message1 = new TutorMessageResponseDdo(
            1L, 1L, null, null
        );

        TutorMessageResponseDdo message2 = new TutorMessageResponseDdo(
            2L, 1L, null, null
        );

        assertThat(result)
            .isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrder(message1,message2)
            .extracting(TutorMessageResponseDdo::getUserId)
            .allMatch(userId -> userId == 1L);
    }

    @Test
    @DisplayName("해당 튜터가 보낸 메시지가 없다.")
    void getMessageTutorList_NOT_FOUND_SEND_MESSAGE() {

        when(messageRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());

        CustomException customException = assertThrows(CustomException.class, () -> {
            tutorMessageService.getMessageTutorList(1L);
        });

        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_SEND_MESSAGE);
    }

    @Test
    @DisplayName("받은 메세지 내용을 지운다.")
    void deleteMessageTutor() {

        Long messageId = 1L;

        TutorMessage message = TutorMessage.builder().messageId(messageId).build();

        when(messageRepository.findByMessageIdOrElseThrow(1L)).thenReturn(message);

        tutorMessageService.deleteMessageTutor(messageId);

        verify(messageRepository, times(1)).delete(message);

    }
}