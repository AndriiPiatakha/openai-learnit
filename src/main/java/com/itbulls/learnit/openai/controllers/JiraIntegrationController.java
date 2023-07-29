package com.itbulls.learnit.openai.controllers;

import java.util.Arrays;
import java.util.List;

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
		return jiraService.createJiraIssue(gson.fromJson(requestBody, JiraIssueFields.class));
	}
	
	// Example: http://localhost:8080/v1/jira/board
	@GetMapping("/v1/jira/board")
	public String getJiraBoards() {
		return jiraService.getJiraBoards();
	}
	
	// Example: http://localhost:8080/v1/jira/board?project=SIB&type=scrum
	@GetMapping(value = "/v1/jira/board", params = {"project", "type"})
	public String getJiraBoards(@RequestParam String project, @RequestParam String type) {
		return jiraService.getJiraBoard(project, type);
	}
	
	// Example: http://localhost:8080/v1/jira/sprint?boardId=2
	@GetMapping(value = "/v1/jira/sprint", params = "boardId")
	public String getSprintsFromBoard(@RequestParam String boardId) {
		return jiraService.getSprints(boardId);
	}
	
	// Example: http://localhost:8080/v1/jira/sprint?boardId=2&lastCompletedAmount=3
	@GetMapping(value = "/v1/jira/sprint", params = {"boardId", "lastCompletedAmount"})
	public String getLastCompletedSprintAmount(@RequestParam String boardId, @RequestParam Integer lastCompletedAmount) {
		return jiraService.getSprints(boardId, lastCompletedAmount);
	}

	// Example: http://localhost:8080/v1/jira/issue?sprints=1,2,3
	@GetMapping(value = "/v1/jira/issue", params = "sprints")
	public String getJiraIssuesFromSprints(@RequestParam String sprints) {
		List<String> sprintIds = Arrays.asList(sprints.split(","));
		return jiraService.getIssuesForSprints(sprintIds);
	}
	
	// Example = http://localhost:8080/v1/jira/issue?lastCompletedSprintsAmount=3
	@GetMapping(value = "/v1/jira/issue", params = "lastCompletedSprintsAmount")
	public Double getAverageVelocityForSpecificAmountOfSprints(@RequestParam Integer lastCompletedSprintsAmount) {
		return jiraService.getAvgVelocity(lastCompletedSprintsAmount);
	}
}
