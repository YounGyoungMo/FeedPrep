package com.example.feedprep.domain.techstack.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class UserTechStackDto {
    private Long relationId;
    private String stackName;
}
