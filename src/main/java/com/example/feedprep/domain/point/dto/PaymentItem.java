package com.example.feedprep.domain.point.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentItem {
	private String paymentId;
	private String status;
	private Amount amount;
}
