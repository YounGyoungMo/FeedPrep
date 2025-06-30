package com.example.feedprep.common.message.service;

import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import java.util.List;

public interface ToturMessageService {

    void createdMessageTuter(Long userId, Long documentId, String fileUrl);

    TutorMessageResponseDdo getMessageTuter(Long messageId);

    List<TutorMessageResponseDdo> getMessageTuterList(Long userId);

    void deleteMessageTuter(Long messageId);
}
