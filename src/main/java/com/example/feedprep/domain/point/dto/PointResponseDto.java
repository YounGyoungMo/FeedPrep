package com.example.feedprep.domain.point.dto;

import java.util.List;

import com.example.feedprep.domain.point.entity.Point;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointResponseDto {

	private List<Point> pointHistory;

	private Integer totalPoint;

	public PointResponseDto(List<Point> pointList, Integer totalPoint) {
		this.pointHistory = pointList;
		this.totalPoint = totalPoint;
	}
}
