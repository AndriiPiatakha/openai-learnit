package com.itbulls.learnit.openai.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.entities.JiraIssueFields;
import com.itbulls.learnit.openai.jira.JiraService;

@RestController
@RequestMapping("/v1/jira")
public class JiraIntegrationController {

	@Autowired
	private Gson gson;
	@Autowired
	private JiraService jiraService;

	// Example: http://localhost:8080/v1/jira/issue?id=LIT-4
	@GetMapping(value = "/issue", params = "id")
	public String getIssueById(@RequestParam("id") String issueId) {
		return jiraService.getFullJsonIssueById(issueId);
	}

	@GetMapping("/issue")
	public String getAllIssuesFromProject() {
		return gson.toJson(jiraService.getJiraIssues());
	}

	@PostMapping("/issue")
	public String createJiraIssue(@RequestBody String requestBody) {
		return jiraService.createJiraIssue(gson.fromJson(requestBody, JiraIssueFields.class));
	}
	
	// Example: http://localhost:8080/v1/jira/board
	@GetMapping("/board")
	public String getJiraBoards() {
		return jiraService.getJiraBoards();
	}
	
	// Example: http://localhost:8080/v1/jira/board?project=SIB&type=scrum
	@GetMapping(value = "/board", params = {"project", "type"})
	public String getJiraBoards(@RequestParam String project, @RequestParam String type) {
		return jiraService.getJiraBoard(project, type);
	}
	
	// Example: http://localhost:8080/v1/jira/sprint?boardId=2
	@GetMapping(value = "/sprint", params = "boardId")
	public String getSprintsFromBoard(@RequestParam String boardId) {
		return jiraService.getSprints(boardId);
	}
	
	// Example: http://localhost:8080/v1/jira/sprint?boardId=2&lastCompletedAmount=3
	@GetMapping(value = "/sprint", params = {"boardId", "lastCompletedAmount"})
	public String getLastCompletedSprintAmount(@RequestParam String boardId, @RequestParam Integer lastCompletedAmount) {
		return jiraService.getSprints(boardId, lastCompletedAmount);
	}

	// Example: http://localhost:8080/v1/jira/issue?sprints=1,2,3
	@GetMapping(value = "/issue", params = "sprints")
	public String getJiraIssuesFromSprints(@RequestParam String sprints) {
		List<String> sprintIds = Arrays.asList(sprints.split(","));
		return jiraService.getIssuesForSprints(sprintIds);
	}
	
	// Example: http://localhost:8080/v1/jira/issue?lastCompletedSprintsAmount=3
	@GetMapping(value = "/issue", params = "lastCompletedSprintsAmount")
	public Double getAverageVelocityForSpecificAmountOfSprints(@RequestParam Integer lastCompletedSprintsAmount) {
		return jiraService.getAvgVelocity(lastCompletedSprintsAmount);
	}
	
	// Example: http://localhost:8080/v1/jira/issue?sprintName=Sprint 2
	@GetMapping(value = "/sprint", params = "sprintName")
	public String getSprintByName(@RequestParam String sprintName) {
		return jiraService.getSprintByName(sprintName);
	}
	
	// Example: http://localhost:8080/v1/jira/issue?sprintName=Sprint%203&workItemId=SIB-201
	@GetMapping(value = "/issue", params = {"sprintName", "workItemId"})
	public String setWorkItemToSprint(@RequestParam String sprintName, @RequestParam String workItemId) {
		return jiraService.setSprintForWorkItem(sprintName, workItemId);
	}
	
	// Example: http://localhost:8080/v1/jira/issue?backlog=true
	@GetMapping(value = "/issue", params = "backlog")
	public String getBacklogItems(@RequestParam String backlog) {
		if (backlog.equalsIgnoreCase(Boolean.TRUE.toString())) {
			return jiraService.getBacklogItems();
		} else {
			return "";
		}
		
	}
	
	// Example: http://localhost:8080/v1/jira/planning?sprintName=Sprint 4&capacity=10
	@GetMapping(value = "/planning", params = {"sprintName", "capacity"})
	public String prepareSprintBasedOnCapacityAndPriority(@RequestParam String sprintName, @RequestParam Integer capacity) {
		return jiraService.planSprintWithCapacityByPriority(sprintName, capacity);
	}
}
