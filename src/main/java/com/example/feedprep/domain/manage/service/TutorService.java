package com.example.feedprep.domain.manage.service;

import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import java.util.List;

public interface TutorService {

    void sendRequestActivity(Long userId, Long adminId, Long documentId);

    List<TutorMessageResponseDdo> getRequestList(Long userId);
}
