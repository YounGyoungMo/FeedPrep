package com.example.feedprep.common.message.dto.response;

import com.example.feedprep.common.message.entity.TutorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TutorMessageResponseDdo {

    private Long messageId;

    private Long userId;

    private Long documentId;

    private String fileName;

    public TutorMessageResponseDdo(TutorMessage toturMessage) {
        this.messageId = toturMessage.getMessageId();
        this.userId = toturMessage.getUserId();
        this.documentId = toturMessage.getDocumentId();
        this.fileName = toturMessage.getFileName();
    }

}
