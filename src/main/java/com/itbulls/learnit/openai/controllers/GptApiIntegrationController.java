package com.itbulls.learnit.openai.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itbulls.learnit.openai.gpt.GptService;

@RestController
public class GptApiIntegrationController {

	
	@Autowired
	private GptService gptService;
	
	@GetMapping("/v1/ask-gpt")
	public String askGpt(@RequestParam String query) {
		return gptService.getAnswerToSingleQuery(query);
	}
	
}
