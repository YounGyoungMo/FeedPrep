package com.example.feedprep.domain.feedbackreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.feedprep.domain.feedbackreview.entity.FeedBackReview;

@Repository
public interface FeedBackReviewRepository extends JpaRepository<FeedBackReview, Long> {
	@Query("SELECT AVG(f.Rating) FROM FeedBackReview f WHERE f.user.userId = :userId")
	Double findAverageRatingByUserId(@Param("userId") Long userId);
}
