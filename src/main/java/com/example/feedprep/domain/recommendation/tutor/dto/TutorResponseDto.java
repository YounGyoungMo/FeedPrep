package com.example.feedprep.domain.recommendation.tutor.dto;

import java.util.List;

import com.example.feedprep.domain.techstack.dto.TechStackResponseDto;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.user.entity.User;
import com.querydsl.core.Tuple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TutorResponseDto {
	private Long tutorId;
	private String name;
	private Double rating;
	@Setter
	private List<String> techStacks;
	private String introduction;

	public TutorResponseDto(Tuple tuple) {
		this.tutorId = tuple.get(0, Long.class);
		this.name = tuple.get(1, String.class);
		this.rating = tuple.get(2, Double.class);
		this.introduction = tuple.get(3, String.class);
	}

	public TutorResponseDto(User tutorInfo, Double tutorRating, List<UserTechStack> tutorTechStack ) {
		this.tutorId = tutorInfo.getUserId();
		this.name = tutorInfo.getName();
		this.rating = tutorRating;
		this.introduction = tutorInfo.getIntroduction();
		this.techStacks = tutorTechStack.stream()
			.map(TechStackResponseDto::new)
			.map(TechStackResponseDto::getTechStack)
			.toList();
	}
}
