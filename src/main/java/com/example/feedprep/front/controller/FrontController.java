package com.example.feedprep.front.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

@Controller
public class FrontController {
	@GetMapping("/charge")
	public String showLoginForm(Model model,
		@Value("${portone.storeId}") String storeId,
		@Value("${portone.channelKey}") String channelKey) {
		model.addAttribute("storeId", storeId);
		model.addAttribute("channelKey", channelKey);
		return "login"; // templates/login.html 렌더링
	}
}
