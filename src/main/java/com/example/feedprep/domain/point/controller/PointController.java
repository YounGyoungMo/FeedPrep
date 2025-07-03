package com.example.feedprep.domain.point.controller;

import com.example.feedprep.common.exception.enums.SuccessCode;
import com.example.feedprep.common.response.ApiResponseDto;
import com.example.feedprep.common.security.annotation.AuthUser;
import com.example.feedprep.domain.point.dto.PointResponseDto;
import com.example.feedprep.domain.point.entity.Point;
import com.example.feedprep.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
@Slf4j
public class PointController {

	private final PointService pointService;

	// 폴링 메서드
	@GetMapping("/check")
	public ResponseEntity<ApiResponseDto<Boolean>> checkCharge(
		@RequestParam String paymentId
	) {

		Boolean isCharge = pointService.checkCharge(paymentId);

		return ResponseEntity.status(SuccessCode.SUCCESS_FETCH_INFO.getHttpStatus())
			.body(ApiResponseDto.success(SuccessCode.SUCCESS_FETCH_INFO, isCharge));
	}

	// 프론트 -> 벡엔드 충전 메서드
	@PostMapping("/charge")
	public ResponseEntity<ApiResponseDto<Void>> pointCharge(
		@AuthUser Long userId,
		@RequestParam String paymentId,
		@RequestParam Integer amount
	) {

		log.info(paymentId);
		pointService.pointCharge(userId, paymentId, amount);

		return ResponseEntity.status(SuccessCode.SUCCESS_FETCH_INFO.getHttpStatus())
			.body(ApiResponseDto.success(SuccessCode.SUCCESS_FETCH_INFO));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponseDto<PointResponseDto>> getPoint(
		@PathVariable Long userId
	){
		List<Point> pointList = pointService.getPointHistory(userId);
		Integer totalPoint = pointService.getPoint(userId);
		PointResponseDto pointResponseDto = new PointResponseDto(pointList, totalPoint);

		return ResponseEntity.status(SuccessCode.TRANSACTION_HISTORY.getHttpStatus())
			.body(ApiResponseDto.success(SuccessCode.TRANSACTION_HISTORY, pointResponseDto));
	}
}
