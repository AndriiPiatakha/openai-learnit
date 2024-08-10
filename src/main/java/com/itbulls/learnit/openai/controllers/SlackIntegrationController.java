package com.itbulls.learnit.openai.controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itbulls.learnit.openai.slack.SlackService;

@RestController
public class SlackIntegrationController {

	@Autowired
	private SlackService slackService;
	
	@Autowired
	private Gson gson;
	
	@Value("${gpt.confluence.bot.enabled}")
	private boolean isConfluenceBotEnabled;
	
	@PostMapping("/v1/slack")
	public String processSlackEvent(@RequestBody String requestBody) {
		System.out.println("Slack Event Received: " + requestBody);
		CompletableFuture.runAsync(() -> {
			if (isConfluenceBotEnabled) {
				slackService.processOnMentionEventViaConfluenceBot(requestBody);
			} else {
				slackService.processOnMentionEvent(requestBody);
			}
		}).whenComplete((result, exception) -> {
			if (exception != null) {
				exception.printStackTrace();
			}
		});
		
		// Return empty String with default 200 OK Status code to confirm receival
		return "";
		
	}

	/*
	 * Use this endpoint to subscribe to events in Slack API the very first time
	 */
//	@PostMapping("/v1/slack")
//	public String processSlackEvent(@RequestBody String requestBody) {
//		System.out.println(requestBody);
//		JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);
//		String challengeValue = jsonObject.get("challenge").getAsString();
//		return challengeValue;
//	}
	
	/*
	 * http://localhost:8080/v1/slack/delete-messages?channelId=C05HJTPMKPX - remove in GPT channel
	 * http://localhost:8080/v1/slack/delete-messages?channelId=C05J5EMSPRC - remove in Gpt-demo channel
	 * 
	 */
	@GetMapping("/v1/slack/delete-messages")
	public String deleteMessagesFromChannel(@RequestParam String channelId) {
		slackService.removeAllMessagesFromChannel(channelId);
		return "deleted";
	}
}
