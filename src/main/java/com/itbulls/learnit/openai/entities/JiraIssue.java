package com.itbulls.learnit.openai.entities;

import java.util.Objects;

public class JiraIssue {
	
	private String key;
	private String assignee;
	private String description;
	private String summary;
	private String status;
	private String dueDate;
	private String projectKey;
	private String projectId;
	private String projectName;
	private String priority;
	private String issueType;
	private Double storyPoints;

	public void setKey(String key) {
		this.key = key;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getKey() {
		return key;
	}

	public String getAssignee() {
		return assignee;
	}

	public String getDescription() {
		return description;
	}

	public String getSummary() {
		return summary;
	}

	public String getStatus() {
		return status;
	}

	public String getDueDate() {
		return dueDate;
	}
	
	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	
	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public Double getStoryPoints() {
		return storyPoints;
	}

	public void setStoryPoints(Double storyPoints) {
		this.storyPoints = storyPoints;
	}

	@Override
	public int hashCode() {
		return Objects.hash(assignee, description, dueDate, issueType, key, priority, projectId, projectKey,
				projectName, status, storyPoints, summary);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JiraIssue other = (JiraIssue) obj;
		return Objects.equals(assignee, other.assignee) && Objects.equals(description, other.description)
				&& Objects.equals(dueDate, other.dueDate) && Objects.equals(issueType, other.issueType)
				&& Objects.equals(key, other.key) && Objects.equals(priority, other.priority)
				&& Objects.equals(projectId, other.projectId) && Objects.equals(projectKey, other.projectKey)
				&& Objects.equals(projectName, other.projectName) && Objects.equals(status, other.status)
				&& Objects.equals(storyPoints, other.storyPoints) && Objects.equals(summary, other.summary);
	}
	
}
