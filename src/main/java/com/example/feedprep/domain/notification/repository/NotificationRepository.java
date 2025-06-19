package com.example.feedprep.domain.notification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.feedprep.domain.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n FROM Notification n where n.receiverId =:receiverId order by n.createdAt DESC LIMIT 10")
	Optional<Notification> findNotificationByReceiverId(Long receiverId);

	@Query("SELECT COUNT(n) FROM Notification n where n.isRead = false")
	Long getCount();
}


