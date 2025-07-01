package com.example.feedprep.common.message.repository;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.message.entity.TutorMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<TutorMessage, Long> {

    Optional<TutorMessage> findByMessageId(Long messageId);

    default TutorMessage findByMessageIdOrElseThrow(Long messageId) {
        return findByMessageId(messageId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_MESSAGE)
        );
    }

    List<TutorMessage> findAllByUserId(Long userId);
}
