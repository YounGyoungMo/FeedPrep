package com.example.feedprep.domain.manage.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import com.example.feedprep.common.message.service.TutorMessageService;
import com.example.feedprep.domain.document.entity.Document;
import com.example.feedprep.domain.document.repository.DocumentRepository;
import com.example.feedprep.domain.notification.service.NotificationService;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TutorServiceImpl implements TutorService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    private final NotificationService notificationService;
    private final TutorMessageService tutorMessageService;

    @Override
    public void sendRequestActivity(Long userId, Long adminId, Long documentId) {

        // 대기중인 듀터가 아님
        User tutor = userRepository.findByIdOrElseThrow(userId);

        if (!tutor.getRole().equals(UserRole.PENDING_TUTOR)) {
            throw new CustomException(ErrorCode.NOT_PENDING_TUTOR);
        }

        // 관리자가 맞는지 확인
        User admin = userRepository.findByIdOrElseThrow(adminId);

        if(!admin.getRole().equals(UserRole.ADMIN)) {
            throw new CustomException(ErrorCode.NOT_EQUALS_ADMIN);
        }

        // 문서 번호와 파일이름 찾기
        Document document = documentRepository.findByIdOrElseThrow(documentId);

        // 메세지 기록 하기
        tutorMessageService.createdMessageTutor(userId,document.getDocumentId(),document.getFileUrl());

        // 요청 알림 보내기
        notificationService.sendNotification(tutor, admin, 201);
    }

    @Override
    public List<TutorMessageResponseDdo> getRequestList(Long userId) {

        return tutorMessageService.getMessageTutorList(userId);
    }
}
