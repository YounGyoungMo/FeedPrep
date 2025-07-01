package com.example.feedprep.common.message.dto.response;

import com.example.feedprep.common.message.entity.TutorMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class TutorMessageResponseDdo {

    private Long messageId;

    private Long userId;

    private Long documentId;

    private String fileName;

    public TutorMessageResponseDdo(TutorMessage tutorMessage) {
        this.messageId = tutorMessage.getMessageId();
        this.userId = tutorMessage.getUserId();
        this.documentId = tutorMessage.getDocumentId();
        this.fileName = tutorMessage.getFileName();
    }

}
