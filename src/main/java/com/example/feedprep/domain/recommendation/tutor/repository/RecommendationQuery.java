package com.example.feedprep.domain.recommendation.tutor.repository;

import java.util.List;

import com.example.feedprep.domain.recommendation.tutor.dto.TutorResponseDto;
import com.querydsl.core.Tuple;

public interface RecommendationQuery {
	List<Tuple> recommendTutors(Long userId, int page);
}
