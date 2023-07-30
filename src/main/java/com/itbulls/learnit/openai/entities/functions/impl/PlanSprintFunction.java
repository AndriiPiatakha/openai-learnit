package com.itbulls.learnit.openai.entities.functions.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.jira.JiraService;

public class PlanSprintFunction implements Function {

	@Autowired
	private JiraService jiraService;
	
	@Override
	public String execute(String arguments) {
		JsonObject jsonObject = JsonParser.parseString(arguments).getAsJsonObject();
        String sprintName = jsonObject.get("sprint_name").getAsString();
        int capacity = jsonObject.get("capacity").getAsInt();
		return jiraService.planSprintWithCapacityByPriority(sprintName, capacity);
	}

}
