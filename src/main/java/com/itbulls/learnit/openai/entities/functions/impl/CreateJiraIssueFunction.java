package com.itbulls.learnit.openai.entities.functions.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.entities.JiraIssueFields;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.jira.JiraService;

public class CreateJiraIssueFunction implements Function {
	
	@Autowired
	private JiraService jiraService;
	@Autowired
	private Gson gson;

	@Override
	public String execute(String arguments) {
		String issueId = jiraService.createJiraIssue(gson.fromJson(arguments, JiraIssueFields.class));
		return String.format("{issue_id_created: %s}", issueId);
	}

}
