package com.itbulls.learnit.openai.entities.functions.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.entities.JiraRisk;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.jira.JiraService;

public class GetRisksFunction implements Function {

	@Autowired
	private JiraService jiraService;
	@Autowired
	private Gson gson;
	
	@Override
	public String execute(String arguments) {
		List<JiraRisk> risks = jiraService.getJiraRisks();
		return gson.toJson(risks);
	}

}
