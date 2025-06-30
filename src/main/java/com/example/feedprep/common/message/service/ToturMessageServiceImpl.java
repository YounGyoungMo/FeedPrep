package com.example.feedprep.common.message.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import com.example.feedprep.common.message.entity.ToturMessage;
import com.example.feedprep.common.message.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToturMessageServiceImpl implements ToturMessageService {

    private final MessageRepository messageRepository;

    @Override
    public void createdMessageTuter(Long userId, Long documentId, String fileUrl) {

        ToturMessage toturMessage = ToturMessage.builder()
            .userId(userId)
            .documentId(documentId)
            .fileName(fileUrl)
            .build();

        messageRepository.save(toturMessage);
    }

    @Override
    public TutorMessageResponseDdo getMessageTuter(Long messageId) {

        ToturMessage tuterToturMessage = messageRepository.findByMessageIdOrElseThrow(messageId);

        return new TutorMessageResponseDdo(tuterToturMessage);
    }

    @Override
    public List<TutorMessageResponseDdo> getMessageTuterList(Long userId) {

        List<ToturMessage> tuterToturMessageList = messageRepository.findAllByUserId(userId);

        if(tuterToturMessageList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SEND_MESSAGE);
        }

        List<TutorMessageResponseDdo> responseDdoList = tuterToturMessageList.stream()
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
        ToturMessage toturMessage = messageRepository.findByMessageIdOrElseThrow(messageId);

        messageRepository.delete(toturMessage);
    }
}
