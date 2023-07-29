package com.itbulls.learnit.openai.jira;

import java.util.List;

import com.itbulls.learnit.openai.entities.JiraIssue;
import com.itbulls.learnit.openai.entities.JiraIssueFields;

public interface JiraService {

	List<JiraIssue> getJiraIssues();
	String getFullJsonIssueById(String issueId);
	String createJiraIssue(JiraIssueFields jiraIssueFields);
	String getJiraBoards();
	String getJiraBoard(String project, String type);
	String getSprints(String boardId);
	String getSprints(String boardId, Integer lastCompletedAmount);
	String getIssuesForSprints(List<String> sprintIds);
	Double getAvgVelocity(Integer lastCompletedSprintsAmount);
}
