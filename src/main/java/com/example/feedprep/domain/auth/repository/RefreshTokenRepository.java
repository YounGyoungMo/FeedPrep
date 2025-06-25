package com.example.feedprep.domain.auth.repository;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.auth.entity.RefreshToken;
import com.example.feedprep.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByUser_UserId(Long userId);

    Optional<RefreshToken> getByUser_UserId(Long userId);

    default RefreshToken getByUser_UserIdOrElseThrow(Long userId) {
        return getByUser_UserId(userId).orElseThrow(()-> new CustomException(ErrorCode.INVALID_TOKEN));
    }

    Long user(User user);
}
