package com.itbulls.learnit.openai.entities.functions.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.entities.JiraIssue;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.jira.JiraService;
import com.itbulls.learnit.openai.jira.impl.DefaultJiraService;

import java.util.*;

public class GetJiraIssuesFunction implements Function {

	@Autowired
	private JiraService jiraService;
	@Autowired
	private Gson gson;
	
	@Override
	public String execute(String arguments) {
		List<JiraIssue> issues = jiraService.getJiraIssues();
		return gson.toJson(issues);
	}

}
