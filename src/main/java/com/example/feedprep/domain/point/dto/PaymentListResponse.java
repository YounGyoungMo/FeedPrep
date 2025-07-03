package com.example.feedprep.domain.point.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentListResponse {
	private List<PaymentItem> items;
}
