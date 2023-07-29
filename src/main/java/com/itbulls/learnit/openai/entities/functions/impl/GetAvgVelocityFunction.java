package com.itbulls.learnit.openai.entities.functions.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonParser;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.jira.JiraService;

public class GetAvgVelocityFunction implements Function {

	@Autowired
	private JiraService jiraService;
	
	@Override
	public String execute(String arguments) {
		Double avgVelocity = jiraService.getAvgVelocity(JsonParser
				.parseString(arguments).getAsJsonObject().get("amount_of_completed_sprints").getAsInt());
		return String.format("The average velocity is %.2f story points per sprint", avgVelocity);
	}

}
