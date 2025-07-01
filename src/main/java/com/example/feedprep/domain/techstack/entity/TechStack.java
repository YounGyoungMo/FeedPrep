package com.example.feedprep.domain.techstack.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.feedprep.domain.techstack.dto.CreateTechStackRequestDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "TechStack")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechStack {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long techId;
	@Column(unique = true)
	private String techStack;

	@OneToMany(mappedBy = "techStack", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserTechStack> userTechStacks;

	public TechStack(String techStack) {
		this.techStack = techStack;
	}

	public void setUserTechStacks(UserTechStack userTechStack) {
		userTechStacks.add(userTechStack);
	}
}
