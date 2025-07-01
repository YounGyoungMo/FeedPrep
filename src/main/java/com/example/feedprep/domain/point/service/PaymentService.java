package com.example.feedprep.domain.point.service;

import com.example.feedprep.domain.point.dto.PaymentResponseDto;

public interface PaymentService {
	PaymentResponseDto getPayment(String paymentId);
}
