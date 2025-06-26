package com.example.feedprep.domain.matching.service;

import com.example.feedprep.domain.matching.dto.MatchingDto;
import com.example.feedprep.domain.matching.repository.MatchingQuery;
import com.example.feedprep.domain.techstack.entity.TechStack;
import com.example.feedprep.domain.techstack.entity.UserTechStack;
import com.example.feedprep.domain.techstack.repository.UserTechStackRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorMatchingServiceImpl implements TutorMatchingService{

	private final UserTechStackRepository userTechStackRepository;

	private final MatchingQuery matchingQuery;

	@Override
	public List<MatchingDto> tutorMatching(Long studentId, int page) {

		List<Tuple> tutorInfo = matchingQuery.getTutor(studentId, page);

		List<MatchingDto> tutorList = tutorInfo.stream().map(MatchingDto::new).toList();

		for(MatchingDto dto : tutorList){
			List<String> tutorTechStack
			 	= userTechStackRepository.findUserTechStackByUser_UserId(dto.getTutorId()).stream().map(UserTechStack::getTechStack)
				.map(TechStack::getTechStack).toList();
			dto.setTechStacks(tutorTechStack);
		}
		return tutorList;
	}
}
