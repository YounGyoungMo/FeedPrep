package com.example.feedprep.common.message.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import com.example.feedprep.common.message.entity.TutorMessage;
import com.example.feedprep.common.message.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TutorMessageServiceImpl implements TutorMessageService {

    private final MessageRepository messageRepository;

    @Override
    public void createdMessageTuter(Long userId, Long documentId, String fileUrl) {

        TutorMessage tutorMessage = TutorMessage.builder()
            .userId(userId)
            .documentId(documentId)
            .fileName(fileUrl)
            .build();

        messageRepository.save(tutorMessage);
    }

    @Override
    public TutorMessageResponseDdo getMessageTuter(Long messageId) {

        TutorMessage tutorMessage = messageRepository.findByMessageIdOrElseThrow(messageId);

        return new TutorMessageResponseDdo(tutorMessage);
    }

    @Override
    public List<TutorMessageResponseDdo> getMessageTuterList(Long userId) {

        List<TutorMessage> TutorMessageList = messageRepository.findAllByUserId(userId);

        if(TutorMessageList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SEND_MESSAGE);
        }

        List<TutorMessageResponseDdo> responseDdoList = TutorMessageList.stream()
            .map(message -> new TutorMessageResponseDdo(
                message.getMessageId(),
                message.getUserId(),
                message.getDocumentId(),
                message.getFileName()
            )).toList();

        return responseDdoList;
    }

    @Override
    public void deleteMessageTuter(Long messageId) {
        TutorMessage tutorMessage = messageRepository.findByMessageIdOrElseThrow(messageId);

        messageRepository.delete(tutorMessage);
    }
}
