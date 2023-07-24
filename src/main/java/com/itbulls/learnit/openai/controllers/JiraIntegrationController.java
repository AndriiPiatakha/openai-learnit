package com.itbulls.learnit.openai.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.entities.JiraIssueFields;
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

	@GetMapping("/v1/jira/issue")
	public String getAllIssuesFromProject() {
		return gson.toJson(jiraService.getJiraIssues());
	}

	@PostMapping("/v1/jira/issue")
	public String createJiraIssue(@RequestBody String requestBody) {
		jiraService.createJiraIssue(gson.fromJson(requestBody, JiraIssueFields.class));
		return "created";
	}

}
