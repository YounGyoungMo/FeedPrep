package com.example.feedprep.domain.point.controller;

import com.example.feedprep.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portone-webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

	@Value("${portone.webhook.secret}")
	private String webhookSecret;

	private final PointService pointService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> receiveWebhook(
		@RequestHeader("x-portone-signature") String signature,
		@RequestHeader("x-portone-timestamp") String timestamp,
		@RequestBody String rawBody) {

		pointService.handleWebhook(webhookSecret, rawBody, signature, timestamp);
		return ResponseEntity.ok().build();
	}
}
