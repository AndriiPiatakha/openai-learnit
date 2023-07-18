package com.itbulls.learnit.openai.jira;

import java.util.List;

import com.itbulls.learnit.openai.entities.JiraIssue;

public interface JiraService {

	List<JiraIssue> getJiraIssues();
	String getFullJsonIssueById(String issueId);

}
