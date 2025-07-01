package com.example.feedprep.domain.user.repository;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.auth.oauth.enums.OAuthProvider;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT t From User t where t.role =:userRole And t.deletedAt IS NULL ")
    List<User>  findByRoleAndDeletedAtIsNull(@Param("userRole")UserRole userRole);


    Optional<User>  findByUserIdAndDeletedAtIsNull(Long userId);
    //피드백 요청용 조회 문
    Optional<User> getUserByEmail(String email);

    default User getUserByEmailOrElseThrow(String email) {
        return getUserByEmail(email).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @EntityGraph(attributePaths = "documents")
    List<User> findAllByRole(UserRole role);

    default User findByIdOrElseThrow(Long id) {
        return findByUserIdAndDeletedAtIsNull(id).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    //피드백 요청용 조회 문
    default User findByIdOrElseThrow(Long id, ErrorCode errorCode) {
        return findByUserIdAndDeletedAtIsNull(id).orElseThrow(
            () -> new CustomException(errorCode)
        );
    }

    Optional<User> findByEmail(String email);

    User findByProviderAndProviderId(OAuthProvider provider, String providerId);
}
