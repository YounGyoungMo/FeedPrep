package com.example.feedprep.domain.feedbackrequestentity.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.feedbackrequestentity.common.RequestState;
import com.example.feedprep.domain.feedbackrequestentity.entity.FeedbackRequestEntity;

@Repository
public interface FeedbackRequestEntityRepository extends JpaRepository<FeedbackRequestEntity, Long>
	, FeedbackRequestEntityRepositoryCustom{


	Optional<FeedbackRequestEntity> findById(Long feedbackRequestId);

	@Query("SELECT f FROM FeedbackRequestEntity f "
		+ "WHERE f.user.userId = :userId "
		+ "AND f.tutor.userId = :tutorId "
		+ "AND f.requestState = :state"
	)
	Optional<FeedbackRequestEntity> findTop1ByUser_UserIdAndTutor_UserIdAndRequestState(
		@Param("userId")Long userId
		,@Param("tutorId")Long tutorId
		,@Param("state") RequestState state);

	@Query("SELECT f FROM FeedbackRequestEntity f where  f.tutor.userId = :tutorId AND f.requestState = :state ")
	Page<FeedbackRequestEntity>  getPagedRequestsForTutor(@Param("tutorId")Long tutorId, @Param("state") RequestState state, PageRequest pageable);

	@Query("SELECT f FROM FeedbackRequestEntity f WHERE  f.tutor.userId = :tutorId AND f.id = :requestId")
	Optional<FeedbackRequestEntity> findPendingByIdAndTutor(@Param("tutorId") Long tutorId, @Param("requestId")Long requestId);


	default FeedbackRequestEntity findByIdOrElseThrow(Long feedbackRequestId){
		return findById(feedbackRequestId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_FEEDBACK_REQUEST));
	}
	default FeedbackRequestEntity findPendingByIdAndTutorOrElseThrow(Long tutorId, Long requestId, ErrorCode errorCode){
		return findPendingByIdAndTutor(tutorId, requestId).orElseThrow(() -> new CustomException(errorCode));
	}
}
