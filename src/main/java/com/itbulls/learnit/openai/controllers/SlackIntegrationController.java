package com.itbulls.learnit.openai.controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.slack.SlackService;

@RestController
public class SlackIntegrationController {

	@Autowired
	private Gson gson;
	
	@Autowired
	private SlackService slackService;
	
	@PostMapping("/v1/slack")
	public String processSlackEvent(@RequestBody String requestBody) {
		System.out.println("Slack Event Received: " + requestBody);
		CompletableFuture.runAsync(() -> {
			slackService.processOnMentionEvent(requestBody);
		}).whenComplete((result, exception) -> {
			if (exception != null) {
				exception.printStackTrace();
			}
		});
		
		// Return empty String with default 200 OK Status code to confirm receival
		return "";
		
	}

}
