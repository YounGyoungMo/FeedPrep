package com.example.feedprep.domain.techstack.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.feedprep.common.config.QueryDslConfig;
import com.example.feedprep.common.exception.base.CustomException;
import com.example.feedprep.common.exception.enums.ErrorCode;
import com.example.feedprep.domain.techstack.entity.TechStack;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class TechStackRepositoryTest {

	@Autowired
	private TechStackRepository techStackRepository;

	@Test
	void findByIdOrElseThrow_성공() {
		techStackRepository.save(new TechStack("Spring Boot"));

		TechStack result = techStackRepository.findByIdOrElseThrow(1L);

		assertThat(result).isNotNull();
		assertThat(result.getTechStack()).isEqualTo("Spring Boot");
	}

	@Test
	void findByIdOrElseThrow_실패() {
		techStackRepository.save(new TechStack("Spring Boot"));

		CustomException exception = assertThrows(CustomException.class, () -> {
			techStackRepository.findByIdOrElseThrow(-1L);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TECH_STACK_NOT_FOUND);
	}
}