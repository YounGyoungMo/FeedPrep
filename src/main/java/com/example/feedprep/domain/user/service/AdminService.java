package com.example.feedprep.domain.user.service;

import com.example.feedprep.common.message.dto.response.TutorMessageResponseDdo;
import com.example.feedprep.domain.techstack.dto.CreateTechStackRequestDto;
import com.example.feedprep.domain.user.dto.response.ApproveTutorResponseDto;
import com.example.feedprep.domain.user.dto.response.TutorResponseDto;

public interface AdminService {

    TutorMessageResponseDdo getMessageTutor(Long messageId);

    ApproveTutorResponseDto approveTutor(Long adminId, Long tutorId, Long messageId);

    void createTechStack(CreateTechStackRequestDto requestDto);

    void deleteTechStack(Long techId);
}
