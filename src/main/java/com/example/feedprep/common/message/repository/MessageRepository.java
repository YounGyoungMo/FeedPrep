package com.example.feedprep.common.message.repository;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.message.entity.ToturMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<ToturMessage, Long> {

    Optional<ToturMessage> findByMessageId(Long messageId);

    default ToturMessage findByMessageIdOrElseThrow(Long messageId) {
        return findByMessageId(messageId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_MESSAGE)
        );
    }

    List<ToturMessage> findAllByUserId(Long userId);
}
