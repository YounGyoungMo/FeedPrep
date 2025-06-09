package com.example.feedprep.domain.feedbackrequestentity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;

@Repository
public interface FeedbackRequestEntityRepository
	extends JpaRepository<FeedbackRequestEntity, Long>, FeedbackRequestEntityRepositoryCustom{}
