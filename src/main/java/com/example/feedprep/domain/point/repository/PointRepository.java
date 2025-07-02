package com.example.feedprep.domain.point.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.feedback.entity.Feedback;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;
import com.example.feedprep.domain.point.entity.Point;
import com.example.feedprep.domain.point.enums.PointType;
import com.example.feedprep.domain.user.entity.User;

public interface PointRepository extends JpaRepository<Point, Long> {
	default Point findByIdOrElseThrow(Long PointId) {
		return findById(PointId)
			.orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
	}

	@Query("SELECT p FROM Point p WHERE p.feedback = :feedback ")
	List<Point> findByFeedback(@Param("feedback") FeedbackRequestEntity feedback);

	Optional<Point> findByPaymentIdAndDeleted(String paymentId, boolean isDelete);

	default Point findByPaymentIdOrElseThrow(String paymentId, boolean isDelete) {
		return findByPaymentIdAndDeleted(paymentId, isDelete).orElseThrow(
			() -> new CustomException(ErrorCode.BAD_REQUEST)
		);
	}

	@Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user.userId = :userId AND p.deleted = false")
	Integer findTotalPointByUserId(@Param("userId") Long userId);

	List<Point> findAllByUser(User user);

	List<Point> findByCreatedAtBetweenAndDeletedFalseAndTypeIn(
		LocalDateTime start,
		LocalDateTime end,
		List<PointType> types
	);
}
