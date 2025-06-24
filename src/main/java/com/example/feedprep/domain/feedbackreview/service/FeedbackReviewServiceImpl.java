package com.example.feedprep.domain.feedbackreview.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.domain.feedback.entity.Feedback;
import com.example.feedprep.domain.feedback.repository.FeedBackRepository;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewRequestDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewResponseDto;
import com.example.feedprep.domain.feedbackreview.entity.FeedbackReview;
import com.example.feedprep.domain.feedbackreview.repository.FeedBackReviewRepository;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackReviewServiceImpl implements FeedbackReviewService {
	private final FeedBackReviewRepository feedBackReviewRepository;
	private final FeedBackRepository feedBackRepository;
    private final UserRepository userRepository;
	private final RedissonClient redissonClient;

	@Autowired
	@Qualifier("ratingTemplate")
	private final RedisTemplate<String, Double> redisTemplate;

	@Qualifier("stringRedisTemplate")
	private final RedisTemplate<String, String> statusTemplate;

	@Transactional
	@Override
	public FeedbackReviewResponseDto createReview( Long userId, Long feedbackId, FeedbackReviewRequestDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		Feedback feedback = feedBackRepository. findWithRequestAndUserById(feedbackId)
			.orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_FEEDBACK));

		if(!feedback.getFeedbackRequestEntity().getUser().getUserId().equals(userId)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		FeedbackReview feedbackReview = new FeedbackReview(dto, feedback, user);
		FeedbackReview saveReview = feedBackReviewRepository.save(feedbackReview);
	    return new FeedbackReviewResponseDto(saveReview);
	}


	@Transactional(readOnly = true)
	@Override
	public FeedbackReviewResponseDto getReview( Long userId, Long reviewId) {
		User user = userRepository.findByIdOrElseThrow(userId);
		FeedbackReview feedbackReview = feedBackReviewRepository.findByIdOrElseThrow(reviewId);
		if(user.getRole().equals(UserRole.STUDENT)){
			if(!feedbackReview.getUserId().equals(userId)){
				throw new CustomException(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS);
			}
		}
		else {
			if(!feedbackReview.getTutorId().equals(userId)){
				throw new CustomException(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS);
			}
		}
		return  new FeedbackReviewResponseDto(feedbackReview);
	}
	@Transactional(readOnly = true)
	@Override
	public List<FeedbackReviewResponseDto> getReviews(Long userId, Integer page, Integer size) {

		User user = userRepository.findByIdOrElseThrow(userId);

		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<FeedbackReview> reviews = null;
		if(user.getRole().equals(UserRole.APPROVED_TUTOR))
		{
			reviews = feedBackReviewRepository.findByTutorIdAndDeletedAtIsNull(user.getUserId(),pageable);
		}
        else {
			reviews =  feedBackReviewRepository.findByUserIdAndDeletedAtIsNull(user.getUserId(),pageable);
		}
		return reviews.stream()
			.map(FeedbackReviewResponseDto ::new)
			.collect(Collectors.toList());
	}

	@Override
	public Double getAverageRating(Long tutorId) {
		User tutor = userRepository.findByIdOrElseThrow(tutorId);
		Double avg = feedBackReviewRepository.getAverageRating(tutor.getUserId());
		return avg != null? avg :0.0;
	}

	@Transactional(readOnly = true)
	@Scheduled(cron = "0 0 5 * * *")
	public void updateRatings () {
		String status = statusTemplate.opsForValue().get("status:updateRatings");
		if (status != null && status.equals("processing")) {
			log.info("[updateRatings] 다른 서버에서 캐싱 중이라 패스합니다.");
			return; // 락 시도 없이 종료
		}

		//락
		RLock lock = redissonClient.getLock("lock:updateRatings");
		boolean isLocked = false;

			try{
				//최대 2초 동안 락 대기, 락 획득 시 3초간 유지( 그 이후 자동 해제 됨)
				isLocked = lock.tryLock(2,3,TimeUnit.SECONDS);

				if(isLocked) {
					statusTemplate.opsForValue().set("status:updateRatings", "processing", 10, TimeUnit.MINUTES);

					List<User> getTutors = userRepository.findAllByRole(UserRole.APPROVED_TUTOR);
					if (!getTutors.isEmpty()) {
						LocalDateTime now = LocalDateTime.now();
						LocalDateTime next5am = LocalDateTime.now().toLocalDate().plusDays(1).atTime(5, 0);
						Duration TTl = Duration.between(now, next5am);
						Long Second = TTl.getSeconds();

						for (User user : getTutors) {
							Long userId = user.getUserId();
							Double avg = getAverageRating(userId); // DB 쿼리 기반 평균 조회
							redisTemplate.opsForValue().set("rating:" + userId.toString(), avg, Second, TimeUnit.SECONDS);

						}
					}
					statusTemplate.opsForValue().set("status:updateRatings", "done", 10, TimeUnit.MINUTES);
					log.info("캐시 완료: {} tutors, TTL: {}s", getTutors.size(), TimeUnit.SECONDS);
					log.info("상태 완료: status:updateRatings = done");
				}
			} catch(InterruptedException ex){
				log.warn("[업데이트 실패] 락 대기 중 인터럽트 발생", ex);
				Thread.currentThread().interrupt(); // 복원
			}catch (Exception ex) {
				statusTemplate.opsForValue().set("status:updateRatings", "error", 10, TimeUnit.MINUTES);
				log.error("[평점 업데이트 실패] 예외 발생", ex);
			}
			finally {
				if(isLocked){
					lock.unlock();
				}
			}
	}
	@Transactional
	@Override
	public FeedbackReviewResponseDto updateReview(Long userId, Long reviewId, FeedbackReviewRequestDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		FeedbackReview feedbackReview = feedBackReviewRepository.findByIdOrElseThrow(reviewId);
        if(!feedbackReview.getUserId().equals(userId)){
			throw new CustomException(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS);
		}
		feedbackReview.updateFeedbackReview(dto);
		FeedbackReview saveReview = feedBackReviewRepository.save(feedbackReview);
		return new FeedbackReviewResponseDto(saveReview);
	}

	@Transactional
	@Override
	public ApiResponseDto deleteReview(Long userId, Long reviewId) {
		User user = userRepository.findByIdOrElseThrow(userId);
		FeedbackReview feedbackReview = feedBackReviewRepository.findByIdOrElseThrow(reviewId);
		if(!feedbackReview.getUserId().equals(userId)){
			throw new CustomException(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS);
		}
		feedbackReview.updateDeletedAt(LocalDateTime.now());
		return new ApiResponseDto(
			SuccessCode.OK_SUCCESS_FEEDBACK_REVIEW_DELETED.getHttpStatus().value(),
			SuccessCode.OK_SUCCESS_FEEDBACK_REVIEW_DELETED.getMessage(),
			SuccessCode.OK_SUCCESS_FEEDBACK_REVIEW_DELETED.getHttpStatus()
			);
	}
}
