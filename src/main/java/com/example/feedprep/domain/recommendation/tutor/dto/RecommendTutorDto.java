package com.example.feedprep.domain.recommendation.tutor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Getter
public class RecommendTutorDto {
	private List<TutorResponseDto> subscribedTutorDto;
	private List<TutorResponseDto> tutorResponseDto;
}
