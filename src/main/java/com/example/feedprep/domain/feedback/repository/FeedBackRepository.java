package com.example.feedprep.domain.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.feedprep.domain.feedback.entity.FeedBack;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
}
