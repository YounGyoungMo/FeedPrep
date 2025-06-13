package com.example.feedprep.domain.user.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.techstack.dto.CreateTechStackRequestDto;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.repository.TechStackRepository;
import com.example.feedprep.domain.user.dto.response.ApproveTutorResponseDto;
import com.example.feedprep.domain.user.dto.response.TutorResponseDto;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;
    private final TechStackRepository techStackRepository;

    @Override
    @Transactional
    public ApproveTutorResponseDto approveTutor(Long tutorId) {

        User user = userRepository.findByIdOrElseThrow(tutorId);

        if(!user.getRole().equals(UserRole.PENDING_TUTOR)){
            throw new CustomException(ErrorCode.NOT_PENDING_TUTOR);
        }

        user.setRole(UserRole.APPROVED_TUTOR);

        return new ApproveTutorResponseDto(user.getRole());
    }

    @Override
    public void createTechStack(CreateTechStackRequestDto requestDto) {

        Optional<TechStack> isTechStack = techStackRepository.findByTechStack(
            requestDto.getTechStack());

        if(isTechStack.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_TECHSTACK);
        }

        TechStack techStack = new TechStack(requestDto.getTechStack());
        techStackRepository.save(techStack);
    }

    @Transactional
    @Override
    public void deleteTechStack(Long techId) {
        TechStack techStack = techStackRepository.findByIdOrElseThrow(techId);
        techStackRepository.delete(techStack);
    }
}
