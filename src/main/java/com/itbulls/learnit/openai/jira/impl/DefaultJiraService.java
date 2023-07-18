package com.itbulls.learnit.openai.jira.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itbulls.learnit.openai.entities.JiraIssue;
import com.itbulls.learnit.openai.jira.JiraService;

@Service
public class DefaultJiraService implements JiraService {
	
	@Value("${jira.api.base.url}") 
	private String jiraApiBaseUrl;
	@Value("${jira.api.resourse.issue}")
	private String issueResourseUrl;
	@Value("${jira.api.resourse.search}")
	private String searchResourseUrl;
	@Value("${jira.api.projet.name}")
	private String jiraProjectName;
	@Value("${jira.token}")
	private String jiraToken;
	@Value("${jira.api.issue.max.results}")
	private Integer maxResults;
	
	@Autowired
	private HttpClient httpClient;
	
	@Override
	public List<JiraIssue> getJiraIssues() {
		/* TODO - during your homework, consider pagination here
		Hints: 
		- in the response you will receive property "total" that will contain total amount of records available to read
		- in the request you can specify "startAt" as part of your JQL to request gradually all remaining records,
		and make enough amount of requests to fetch all the necessary data.
		- maxResults attribute contains the amount of records available in the current request
		*/
		String requestUrl = jiraApiBaseUrl + searchResourseUrl + "?jql=project=" + jiraProjectName + "&maxResults=" + maxResults;
		HttpGet request = new HttpGet(requestUrl);
		request.setHeader("Content-Type", "application/json");
		request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + jiraToken);

		List<JiraIssue> jiraIssuesList = new ArrayList<>();

		try {
		    HttpResponse response = httpClient.execute(request);
		    HttpEntity entity = response.getEntity();
		    String responseString = EntityUtils.toString(entity);

		    // Parse JSON response using Gson's JsonParser
		    JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
		    JsonArray issuesArray = jsonObject.getAsJsonArray("issues");

		    for (JsonElement issueElement : issuesArray) {
		        JsonObject issueObject = issueElement.getAsJsonObject();
		        JiraIssue jiraIssue = new JiraIssue();
		        jiraIssue.setKey(issueObject.get("key").getAsString());
		        JsonObject fields = issueObject.getAsJsonObject("fields");
		        if (fields.has("assignee") && !fields.get("assignee").isJsonNull()) {
		            JsonObject assigneeJsonObject = fields.getAsJsonObject("assignee");
		            if (assigneeJsonObject.has("displayName") && !assigneeJsonObject.get("displayName").isJsonNull()) {
		                jiraIssue.setAssignee(assigneeJsonObject.get("displayName").getAsString());
		            }
		        }
		        jiraIssue.setStatus(fields.getAsJsonObject("status").get("name").getAsString());
		        if (fields.has("description") && !fields.get("description").isJsonNull()) {
		            jiraIssue.setDescription(fields.get("description").getAsString());
		        }
		        jiraIssue.setSummary(fields.get("summary").getAsString());
		        if (fields.has("duedate") && !fields.get("duedate").isJsonNull()) {
		            jiraIssue.setDueDate(fields.get("duedate").getAsString());
		        }
		        JsonObject project = fields.getAsJsonObject("project");
		        jiraIssue.setProjectId(project.get("id").getAsString());
		        jiraIssue.setProjectKey(project.get("key").getAsString());
		        jiraIssue.setProjectName(project.get("name").getAsString());

		        JsonObject priority = fields.getAsJsonObject("priority");
		        jiraIssue.setPriority(priority.get("name").getAsString());

		        JsonObject issueType = fields.getAsJsonObject("issuetype");
		        jiraIssue.setIssueType(issueType.get("name").getAsString());

		        jiraIssuesList.add(jiraIssue);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		
		return jiraIssuesList;
	}

	@Override
	public String getFullJsonIssueById(String issueId) {
	    String url = jiraApiBaseUrl + issueResourseUrl + "/" + issueId;
        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + jiraToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        try {
            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
	}

}
