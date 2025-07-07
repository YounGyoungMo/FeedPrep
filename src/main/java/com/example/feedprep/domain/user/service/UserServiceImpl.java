package com.example.feedprep.domain.user.service;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.techstack.dto.UserTechStackDto;
import com.example.feedprep.domain.user.dto.request.NewPasswordRequestDto;
import com.example.feedprep.domain.user.dto.request.UpdateMyInfoRequestDto;
import com.example.feedprep.domain.user.dto.response.PasswordModifiedAtResponseDto;
import com.example.feedprep.domain.user.dto.response.TutorResponseDto;
import com.example.feedprep.domain.user.dto.response.UserResponseDto;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<TutorResponseDto> getTutorList() {

        List<User> tutorList = userRepository.findAllByRole(UserRole.APPROVED_TUTOR);

        if(tutorList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TUTOR);
        }

        List<TutorResponseDto> responseDtoList = tutorList.stream()
            .map(user -> {
                List<UserTechStackDto> userTechStackDtos = user.getUserTechStacks().stream()
                    .map(uts -> new UserTechStackDto(
                        uts.getRelationId(),
                        uts.getTechStack().getTechStack()
                    )).toList();

                return new TutorResponseDto(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getRating(),
                    userTechStackDtos
                );
            }).toList();

        return responseDtoList;
    }

    @Override
    public UserResponseDto getMyInfo(Long userId) {

        User user = userRepository.findByIdOrElseThrow(userId);

        UserResponseDto responseDto = new UserResponseDto(user);

        return responseDto;
    }

    @Override
    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UpdateMyInfoRequestDto requestDto) {

        User user = userRepository.findByIdOrElseThrow(userId);

        Optional.ofNullable(requestDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(requestDto.getAddress()).ifPresent(user::setAddress);
        Optional.ofNullable(requestDto.getIntroduction()).ifPresent(user::setIntroduction);

        UserResponseDto responseDto = new UserResponseDto(user);

        return responseDto;
    }

    @Override
    @Transactional
    public PasswordModifiedAtResponseDto changePassword(Long userId, NewPasswordRequestDto requestDto) {

        User user = userRepository.findByIdOrElseThrow(userId);

        if(!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));

        PasswordModifiedAtResponseDto responseDto = new PasswordModifiedAtResponseDto(
            user.getModifiedAt());

        return responseDto;
    }
}
