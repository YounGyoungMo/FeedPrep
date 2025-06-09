package com.example.feedprep.domain.recommendation.tutor.service;

import java.util.List;

import com.example.feedprep.domain.recommendation.tutor.dto.RecommendTutorDto;
import com.example.feedprep.domain.recommendation.tutor.dto.TutorResponseDto;

public interface TutorRecommendationService {
	RecommendTutorDto recommendTutors(Long studentId, int page);
}
