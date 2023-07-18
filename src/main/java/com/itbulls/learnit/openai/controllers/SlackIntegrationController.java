package com.itbulls.learnit.openai.controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itbulls.learnit.openai.slack.SlackService;

@RestController
public class SlackIntegrationController {

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

	
	/*
	 * http://localhost:8080/v1/slack/delete-messages?channelId=C05HJTPMKPX - remove in GPT channel
	 */
	@GetMapping("/v1/slack/delete-messages")
	public String deleteMessagesFromChannel(@RequestParam String channelId) {
		slackService.removeAllMessagesFromChannel(channelId);
		return "deleted";
	}
}
