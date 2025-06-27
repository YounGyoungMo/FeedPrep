package com.example.feedprep.domain.point.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PaymentResponseDto {
	private String id;
	private String merchantUid;
	private String status;
	private Amount amount;

	@Getter
	@Setter
	public static class Amount {
		private Integer total;      // 총 결제 금액
	}
}
