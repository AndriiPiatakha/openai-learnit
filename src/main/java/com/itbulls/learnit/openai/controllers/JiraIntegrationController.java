package com.itbulls.learnit.openai.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.jira.JiraService;

@RestController
public class JiraIntegrationController {
	
	@Autowired
	private Gson gson;
	@Autowired
	private JiraService jiraService;

	// Example: http://localhost:8080/v1/jira/issue?id=LIT-4
	@GetMapping(value = "/v1/jira/issue", params = "id")
	public String getIssueById(@RequestParam("id") String issueId) {
		return jiraService.getFullJsonIssueById(issueId);
	}
	
	@GetMapping(value = "/v1/jira/issue")
	public String getAllIssuesFromProject() {
		return gson.toJson(jiraService.getJiraIssues());
	}

}
