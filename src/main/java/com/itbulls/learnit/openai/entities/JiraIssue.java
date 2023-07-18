package com.itbulls.learnit.openai.entities;

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

	
	@Override
	public String toString() {
		return "JiraIssue [key=" + key + ", assignee=" + assignee + ", description=" + description + ", summary="
				+ summary + ", status=" + status + ", dueDate=" + dueDate + ", projectKey=" + projectKey
				+ ", projectId=" + projectId + ", projectName=" + projectName + ", priority=" + priority
				+ ", issueType=" + issueType + "]";
	}

}
