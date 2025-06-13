package com.example.feedprep.domain.user.service;

import com.example.feedprep.domain.techstack.dto.CreateTechStackRequestDto;
import com.example.feedprep.domain.user.dto.response.ApproveTutorResponseDto;
import com.example.feedprep.domain.user.dto.response.TutorResponseDto;

public interface AdminService {

    ApproveTutorResponseDto approveTutor(Long tutorId);

    void createTechStack(CreateTechStackRequestDto requestDto);

    void deleteTechStack(Long techId);
}
