package com.example.feedprep.domain.feedbackreview.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.feedbackreview.entity.FeedbackReview;

@Repository
public interface FeedBackReviewRepository extends JpaRepository<FeedbackReview, Long> {



	Optional<FeedbackReview> findById(Long id);

	//학생이 작성한 리뷰를 확인
	@Query("SELECT fr FROM FeedbackReview fr WHERE fr.userId = :userId AND fr.deletedAt IS null ")
	Page<FeedbackReview> findByUserIdAndDeletedAtIsNull(@Param("userId")Long userId, PageRequest pageable);

	//튜터에게 작성한 학생들의 리뷰를 조회
	@Query("SELECT fr FROM FeedbackReview fr WHERE fr.tutorId = :tutorId AND fr.deletedAt IS null ")
	Page<FeedbackReview> findByTutorIdAndDeletedAtIsNull(@Param("tutorId")Long tutorId, PageRequest pageable);

	@Query("SELECT AVG(fr.rating) FROM FeedbackReview fr WHERE fr.tutorId = :tutorId AND fr.deletedAt IS null ")
	Double getAverageRating(@Param("tutorId") Long tutorId);

	default FeedbackReview findByIdOrElseThrow(Long id){
		return findById(id).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEEDBACK_REVIEW));
	}
}
