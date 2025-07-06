package com.example.feedprep.domain.feedback.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.feedprep.domain.feedback.entity.Feedback;

@Repository
public interface FeedBackRepository extends JpaRepository<Feedback, Long> {

	@Query("SELECT f FROM Feedback f JOIN FETCH f.feedbackRequestEntity fr JOIN FETCH fr.user WHERE f.id = :id")
	Optional<Feedback> findWithRequestAndUserById(Long id);

	boolean existsFeedbackByFeedbackRequestEntityIdAndTutorId(Long feedbackRequestEntityId, Long tutorId);
}
