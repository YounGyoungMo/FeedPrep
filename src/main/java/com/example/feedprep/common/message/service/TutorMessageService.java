package com.example.feedprep.common.message.service;

import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import java.util.List;

public interface TutorMessageService {

    void createdMessageTutor(Long userId, Long documentId, String fileUrl);

    TutorMessageResponseDdo getMessageTutor(Long messageId);

    List<TutorMessageResponseDdo> getMessageTutorList(Long userId);

    void deleteMessageTutor(Long messageId);
}
