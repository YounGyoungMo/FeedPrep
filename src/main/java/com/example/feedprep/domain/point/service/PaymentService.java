package com.example.feedprep.domain.point.service;

import java.util.List;

import com.example.feedprep.domain.point.dto.PaymentResponseDto;
import com.example.feedprep.domain.point.dto.PaymentSummaryDto;

public interface PaymentService {
	PaymentResponseDto getPayment(String paymentId);

	List<PaymentSummaryDto> getAllPayment();
}
