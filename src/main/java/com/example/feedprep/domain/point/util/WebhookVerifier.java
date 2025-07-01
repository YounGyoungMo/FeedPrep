package com.example.feedprep.domain.point.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class WebhookVerifier {
	public static boolean verify(String secret, String body, String signature, String timestamp) {
		try {
			String baseString = timestamp + "." + body;
			Mac hmacSha256 = Mac.getInstance("HmacSHA256");
			hmacSha256.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] hash = hmacSha256.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
			String expectedSignature = Base64.getEncoder().encodeToString(hash);
			return MessageDigest.isEqual(expectedSignature.getBytes(), signature.getBytes());
		} catch (Exception e) {
			return false;
		}
	}
}
