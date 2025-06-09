package com.example.feedprep.domain.techstack.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.feedprep.domain.techstack.dto.CreateTechStackRequestDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "TechStack")
@Getter
@NoArgsConstructor
public class TechStack {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long techId;
	private String techStack;

	@OneToMany(mappedBy = "techStack", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserTechStack> userTechStacks = new ArrayList<>();

	public TechStack(String techStack) {
		this.techStack = techStack;
	}

	public void setUserTechStacks(UserTechStack userTechStack) {
		userTechStacks.add(userTechStack);
	}
}
