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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itbulls.learnit.openai.entities.CreateJiraIssueRequest;
import com.itbulls.learnit.openai.entities.JiraIssue;
import com.itbulls.learnit.openai.entities.JiraIssueFields;
import com.itbulls.learnit.openai.jira.JiraService;

@Service
public class DefaultJiraService implements JiraService {

	@Value("${jira.api.base.url}")
	private String jiraApiBaseUrl;
	@Value("${jira.api.resourse.issue}")
	private String issueResourseUrl;
	@Value("${jira.api.resourse.search}")
	private String searchResourseUrl;
	@Value("${jira.api.resourse.issuetype}")
	private String issueTypeResourceUrl;
	@Value("${jira.api.projet.name}")
	private String jiraProjectName;
	@Value("${jira.api.resource.users}")
	private String userResourceUrl;
	@Value("${jira.api.resourse.users.search}")
	private String userSearchResourceUrl;
	@Value("${jira.token}")
	private String jiraToken;
	@Value("${jira.api.issue.max.results}")
	private Integer maxResults;

	@Autowired
	private HttpClient httpClient;
	@Autowired
	private Gson gson;

	@Override
	public List<JiraIssue> getJiraIssues() {
		/*
		 * TODO - during your homework, consider pagination here Hints: - in the
		 * response you will receive property "total" that will contain total amount of
		 * records available to read - in the request you can specify "startAt" as part
		 * of your JQL to request gradually all remaining records, and make enough
		 * amount of requests to fetch all the necessary data. - maxResults attribute
		 * contains the amount of records available in the current request
		 */
		String requestUrl = jiraApiBaseUrl + searchResourseUrl + "?jql=project=" + jiraProjectName + "&maxResults="
				+ maxResults;
		HttpGet request = new HttpGet(requestUrl);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
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

	@Override
	public String createJiraIssue(JiraIssueFields jiraIssueFields) {
		String url = jiraApiBaseUrl + issueResourseUrl;
		HttpPost httpPost = new HttpPost(url);
		System.out.println(gson.toJson(jiraIssueFields));
		httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + jiraToken);
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		CreateJiraIssueRequest jiraRequestBody = new CreateJiraIssueRequest();
		var fields = jiraRequestBody.new Fields();

		fields.setAssigneee(fields.new Assignee(getJiraUserIdByDisplayName(jiraIssueFields.getAssignee())));

		fields.setIssuetype(fields.new IssueType(jiraIssueFields.getIssueType()));
		fields.setProject(fields.new Project(jiraProjectName));
		fields.setDescription(jiraIssueFields.getDescription());
		fields.setDuedate(jiraIssueFields.getDueDate());
		fields.setSummary(jiraIssueFields.getSummary());

		jiraRequestBody.setFields(fields);

		StringEntity stringEntity = new StringEntity(gson.toJson(jiraRequestBody), "UTF-8");
		httpPost.setEntity(stringEntity);

		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			/*
			 * Example of response:
				{
				    "id": "10027",
				    "key": "PA-27",
				    "self": "https://andrey-pyatakha.atlassian.net/rest/api/2/issue/10027"
				}
			 */
			String response = EntityUtils.toString(httpEntity);
			System.out.println(response);
	        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
	        if (jsonObject.has("key")) {
	        	String key = jsonObject.get("key").getAsString();
		        return key;
	        } else {
	        	return response;
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getJiraUserIdByDisplayName(String assigneeName) {
		String url = jiraApiBaseUrl + userResourceUrl + userSearchResourceUrl;
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + jiraToken);
		httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			String response = EntityUtils.toString(httpEntity);
	        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
	        for (JsonElement jsonElement : jsonArray) {
	            JsonObject jsonObject = jsonElement.getAsJsonObject();
	            String displayName = jsonObject.get("displayName").getAsString();
	            
	            /* TODO - potential area for improvement is to send request to GPT
	            in order to select the correct user based on the context and minor changes in spelling.
	            Because in current implementation we need the exact match when we specify the name of
	            the team member we want to assign the work item to.
	            */
	            if (displayName.equalsIgnoreCase(assigneeName)) {
	            	return jsonObject.get("accountId").getAsString();
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
