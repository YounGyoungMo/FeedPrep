package com.example.feedprep.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Optional<Notification> findById(Long id);

	List<Notification> findByCreatedAtBeforeOrderByCreatedAtAsc(LocalDateTime threshold);

	@Query("SELECT n FROM Notification n where n.receiverId =:receiverId")
	Page<Notification> findNotificationByReceiverId(@Param("receiverId") Long receiverId, PageRequest pageRequest);

	@Query("SELECT COUNT(n) FROM Notification n WHERE n.receiverId = :receiverId AND n.isRead = false AND n.isStale = true")
	Long getCountByReceiver(@Param("receiverId") Long receiverId);


	default Notification findByIdOrElseThrow(Long notificationId){
		return findById(notificationId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION));
	}
}


