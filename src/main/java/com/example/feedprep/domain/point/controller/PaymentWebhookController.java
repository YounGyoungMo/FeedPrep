package com.example.feedprep.domain.point.controller;

import com.example.feedprep.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portone-webhook")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

	@Value("${portone.webhook.secret}")
	private String webhookSecret;

	private final PointService pointService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> receiveWebhook(
		// @RequestHeader("webhook-signature") String signature,
		// @RequestHeader("webhook-timestamp") String timestamp,
		@RequestBody String rawBody) {
		log.error("예외처리 컨트롤러");
		String signature = "signature";
		String timestamp = "timestamp";
		pointService.handleWebhook(webhookSecret, rawBody, signature, timestamp);
		return ResponseEntity.ok().build();
	}
}
