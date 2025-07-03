package com.example.feedprep.domain.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentSummaryDto {
	private String paymentId;
	private String status;
	private int totalAmount;
}
