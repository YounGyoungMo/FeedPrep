package com.example.feedprep.domain.point.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.feedprep.domain.point.dto.PaymentResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
	private final RestTemplate restTemplate;

	@Value("${PORTONE_APISECRET}")
	private String apiSecret;

	@Value("${PORTONE_STOREID}")
	private String storeId;

	@Override
	public PaymentResponseDto getPayment(String paymentId) {
		String url = UriComponentsBuilder
			.fromHttpUrl("https://api.portone.io/payments/" + paymentId)
			.queryParam("storeId", storeId)  // storeId 변수로 넣기
			.toUriString();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "PortOne " + apiSecret);
		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<PaymentResponseDto> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			request,
			PaymentResponseDto.class
		);

		return response.getBody();
	}
}
