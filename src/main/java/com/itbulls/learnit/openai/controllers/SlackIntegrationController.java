package com.itbulls.learnit.openai.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
public class SlackIntegrationController {

	@Autowired
	private Gson gson;
	
	@PostMapping("/v1/slack")
	public String processSlackEvent(@RequestBody String requestBody) {
		System.out.println(requestBody);
		JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);
		String challengeValue = jsonObject.get("challenge").getAsString();
		return challengeValue;
	}
}
