package com.example.feedprep.domain.point.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.feedprep.domain.point.dto.PaymentItem;
import com.example.feedprep.domain.point.dto.PaymentListResponse;
import com.example.feedprep.domain.point.dto.PaymentResponseDto;
import com.example.feedprep.domain.point.dto.PaymentSummaryDto;
import com.example.feedprep.domain.point.dto.TokenResponse;

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

	@Override
	public List<PaymentSummaryDto> getAllPayment() {
		// 1. AccessToken 요청
		String tokenUrl = "https://api.portone.io/login/api-secret";
		HttpHeaders tokenHeaders = new HttpHeaders();
		tokenHeaders.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> tokenBody = new HashMap<>();
		tokenBody.put("apiSecret", apiSecret);

		HttpEntity<Map<String, String>> tokenRequest = new HttpEntity<>(tokenBody, tokenHeaders);
		ResponseEntity<TokenResponse> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, TokenResponse.class);

		String accessToken = Objects.requireNonNull(tokenResponse.getBody()).getAccessToken();

		// 2. 결제 리스트 요청
		String paymentsUrl = "https://api.portone.io/payments/search";
		HttpHeaders paymentHeaders = new HttpHeaders();
		paymentHeaders.setContentType(MediaType.APPLICATION_JSON);
		paymentHeaders.setBearerAuth(accessToken);

		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

		ZonedDateTime now = ZonedDateTime.now();                    // 현재 시각
		ZonedDateTime yesterday = now.minusDays(1);

		Map<String, Object> filter = new HashMap<>();
		filter.put("from", yesterday.format(formatter));           // 하루 전
		filter.put("until", now.format(formatter));
		filter.put("status", List.of("PAID"));

		Map<String, Object> page = new HashMap<>();
		page.put("number", 0);
		page.put("size", 100);

		Map<String, Object> searchBody = new HashMap<>();
		searchBody.put("page", page);
		searchBody.put("filter", filter);

		HttpEntity<Map<String, Object>> paymentRequest = new HttpEntity<>(searchBody, paymentHeaders);

		ResponseEntity<PaymentListResponse> paymentResponse = restTemplate.exchange(
			paymentsUrl,
			HttpMethod.POST,
			paymentRequest,
			PaymentListResponse.class
		);

		// 3. 원하는 필드 추출 (paymentId, status, amount.total)
		List<PaymentSummaryDto> result = new ArrayList<>();
		for (PaymentItem item : Objects.requireNonNull(paymentResponse.getBody()).getItems()) {
			PaymentSummaryDto summary = new PaymentSummaryDto(
				item.getPaymentId(),
				item.getStatus(),
				item.getAmount().getTotal()
			);
			result.add(summary);
		}
		return result;
	}
}
