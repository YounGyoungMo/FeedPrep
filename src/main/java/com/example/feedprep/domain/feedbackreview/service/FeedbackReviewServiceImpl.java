package com.example.feedprep.domain.feedbackreview.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.example.feedprep.common.s3.service.S3ServiceImpl;
import com.example.feedprep.domain.feedback.entity.Feedback;
import com.example.feedprep.domain.feedback.repository.FeedBackRepository;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewListDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewRequestDto;
import com.example.feedprep.domain.feedbackreview.dto.FeedbackReviewDetailsDto;
import com.example.feedprep.domain.feedbackreview.entity.FeedbackReview;
import com.example.feedprep.domain.feedbackreview.repository.FeedBackReviewRepository;
import com.example.feedprep.domain.notification.service.NotificationServiceImpl;
import com.example.feedprep.domain.user.entity.User;
import com.example.feedprep.domain.user.enums.UserRole;
import com.example.feedprep.domain.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class FeedbackReviewServiceImpl implements FeedbackReviewService {
	private final FeedBackReviewRepository feedBackReviewRepository;
	private final FeedBackRepository feedBackRepository;
    private final UserRepository userRepository;
	private final RedissonClient redissonClient;
    private final NotificationServiceImpl notificationService;

	private static final Logger slackLogger = LoggerFactory.getLogger(S3ServiceImpl.class);

	@Qualifier("ratingTemplate")
	private final RedisTemplate<String, Double> redisTemplate;

	@Qualifier("stringRedisTemplate")
	private final RedisTemplate<String, String> statusTemplate;

	@Transactional
	@Override
	public FeedbackReviewDetailsDto createReview( Long userId, Long feedbackId, FeedbackReviewRequestDto dto) {

		if(feedBackReviewRepository.existsByFeedbackIdAndUserId(feedbackId, userId)){
			throw new CustomException(ErrorCode.DUPLICATE_FEEDBACK_REVIEW);
		}
		User user = userRepository.findByIdOrElseThrow(userId);
		if(!user.getRole().equals(UserRole.STUDENT)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		Feedback feedback = feedBackRepository. findWithRequestAndUserById(feedbackId)
			.orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_FEEDBACK));
		User tutor = feedback.getTutor();
		if(!feedback.getFeedbackRequestEntity().getUser().getUserId().equals(userId)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		FeedbackReview feedbackReview = new FeedbackReview(dto, feedback, user);
		FeedbackReview saveReview = feedBackReviewRepository.save(feedbackReview);
		notificationService.sendNotification(user, tutor,105 );
	    return new FeedbackReviewDetailsDto(saveReview);
	}


	@Transactional(readOnly = true)
	@Override
	public FeedbackReviewDetailsDto getReview( Long userId, Long reviewId) {
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
		return  new FeedbackReviewDetailsDto(feedbackReview);
	}
	@Transactional(readOnly = true)
	@Override
	public List<FeedbackReviewListDto> getReviews(Long userId, Integer page, Integer size) {

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
			.map(FeedbackReviewListDto::new)
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
			slackLogger.info("[updateRatings] 다른 서버에서 캐싱 중이라 패스합니다.");
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
							Double avg =  feedBackReviewRepository.getAverageRating(userId);
							double rounded =(avg == null)? 0.0: Math.round(avg * 10) / 10.0;
							redisTemplate.opsForValue().set("rating:" + userId.toString(), rounded, Second, TimeUnit.SECONDS);
						}
					}
					statusTemplate.opsForValue().set("status:updateRatings", "done", 10, TimeUnit.MINUTES);
					slackLogger.info("상태 완료: status:updateRatings = done 캐시 완료: {} tutors, TTL: {}s", getTutors.size(), TimeUnit.SECONDS);
				}
			} catch(InterruptedException ex){
				slackLogger.warn("[업데이트 실패] 락 대기 중 인터럽트 발생", ex);
				Thread.currentThread().interrupt(); // 복원
			}catch (Exception ex) {
				statusTemplate.opsForValue().set("status:updateRatings", "error", 10, TimeUnit.MINUTES);
				slackLogger.error("[평점 업데이트 실패] 예외 발생", ex);
			}
			finally {
				if(isLocked){
					lock.unlock();
				}
			}
	}
	@Transactional
	@Override
	public FeedbackReviewDetailsDto updateReview(Long userId, Long reviewId, FeedbackReviewRequestDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		if(!user.getRole().equals(UserRole.STUDENT)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		FeedbackReview feedbackReview = feedBackReviewRepository.findByIdOrElseThrow(reviewId);
        if(!feedbackReview.getUserId().equals(userId)){
			throw new CustomException(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS);
		}
		feedbackReview.updateFeedbackReview(dto);
		FeedbackReview saveReview = feedBackReviewRepository.save(feedbackReview);
		return new FeedbackReviewDetailsDto(saveReview);
	}

	@Transactional
	@Override
	public Map<String, Object> deleteReview(Long userId, Long reviewId) {
		User user = userRepository.findByIdOrElseThrow(userId);
		if(!user.getRole().equals(UserRole.STUDENT)){
			throw new CustomException(ErrorCode.UNAUTHORIZED_REQUESTER_ACCESS);
		}
		FeedbackReview feedbackReview = feedBackReviewRepository.findByIdOrElseThrow(reviewId);
		if(!feedbackReview.getUserId().equals(userId)){
			throw new CustomException(ErrorCode.FOREIGN_REQUESTER_REVIEW_ACCESS);
		}
		feedbackReview.updateDeletedAt(LocalDateTime.now());
		Map<String, Object> data = new HashMap<>();
		data.put("date", feedbackReview.getDeletedAt());
		return data;
	}
}
