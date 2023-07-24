package com.itbulls.learnit.openai.jira;

import java.util.List;

import com.itbulls.learnit.openai.entities.JiraIssue;
import com.itbulls.learnit.openai.entities.JiraIssueFields;

public interface JiraService {

	List<JiraIssue> getJiraIssues();
	String getFullJsonIssueById(String issueId);
	String createJiraIssue(JiraIssueFields jiraIssueFields);

}
