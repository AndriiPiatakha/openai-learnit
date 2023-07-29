package com.itbulls.learnit.openai.jira.impl;

import static com.itbulls.learnit.openai.entities.JiraSprint.CLOSED_STATE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.itbulls.learnit.openai.entities.JiraSprint;
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
	@Value("${jira.api.agile.base.url}")
	private String jiraApiAgileBaseUrl;
	@Value("${jira.api.agile.resource.board}")
	private String boardResourceUrl;
	@Value("${jira.api.agile.resource.sprint}")
	private String sprintResourceUrl;
	@Value("${jira.api.agile.board.type.scrum}")
	private String scrumBoardType;
	@Value("${jira.terminal.status}")
	private String doneStatus;

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
		List<JiraIssue> jiraIssuesList = new ArrayList<>();
		String responseString = executeGetRequest(requestUrl);

		// Parse JSON response using Gson's JsonParser
		JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
		JsonArray issuesArray = jsonObject.getAsJsonArray("issues");

		for (JsonElement issueElement : issuesArray) {
			JsonObject issueObject = issueElement.getAsJsonObject();
			JiraIssue jiraIssue = convertJsonToJiraIssue(issueObject);
			jiraIssuesList.add(jiraIssue);
		}
		return jiraIssuesList;
	}

	private JiraIssue convertJsonToJiraIssue(JsonObject issueObject) {
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
		
		// this is story points field in Jira API
		if (fields.has("customfield_10034") && !fields.get("customfield_10034").isJsonNull()) {
			jiraIssue.setStoryPoints(Double.valueOf(fields.get("customfield_10034").getAsString()));
		}
		JsonObject project = fields.getAsJsonObject("project");
		jiraIssue.setProjectId(project.get("id").getAsString());
		jiraIssue.setProjectKey(project.get("key").getAsString());
		jiraIssue.setProjectName(project.get("name").getAsString());

		JsonObject priority = fields.getAsJsonObject("priority");
		jiraIssue.setPriority(priority.get("name").getAsString());

		JsonObject issueType = fields.getAsJsonObject("issuetype");
		jiraIssue.setIssueType(issueType.get("name").getAsString());
		return jiraIssue;
	}

	@Override
	public String getFullJsonIssueById(String issueId) {
		String url = jiraApiBaseUrl + issueResourseUrl + "/" + issueId;
		return executeGetRequest(url);
	}

	@Override
	public String createJiraIssue(JiraIssueFields jiraIssueFields) {
		String url = jiraApiBaseUrl + issueResourseUrl;
		HttpPost httpPost = new HttpPost(url);
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

		String response = "";
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			/*
			 * Example of response: { "id": "10027", "key": "PA-27", "self":
			 * "https://andrey-pyatakha.atlassian.net/rest/api/2/issue/10027" }
			 */
			response = EntityUtils.toString(httpEntity);
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			if (jsonObject.has("key")) {
				String key = jsonObject.get("key").getAsString();
				return key;
			} else {
				return response;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return response;
		}
	}

	private String getJiraUserIdByDisplayName(String assigneeName) {
		String url = jiraApiBaseUrl + userResourceUrl + userSearchResourceUrl;
		String response = executeGetRequest(url);
		JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
		for (JsonElement jsonElement : jsonArray) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			String displayName = jsonObject.get("displayName").getAsString();

			/*
			 * TODO - potential area for improvement is to send request to GPT in order to
			 * select the correct user based on the context and minor changes in spelling.
			 * Because in current implementation we need the exact match when we specify the
			 * name of the team member we want to assign the work item to.
			 */
			if (displayName.equalsIgnoreCase(assigneeName)) {
				return jsonObject.get("accountId").getAsString();
			}
		}
		return null;
	}

	@Override
	public String getJiraBoards() {
		String url = jiraApiAgileBaseUrl + boardResourceUrl;
		return executeGetRequest(url);
	}

	/*
	 * In current implementation there is an assumption that you would have only one
	 * board per project with specific type
	 */
	@Override
	public String getJiraBoard(String project, String type) {
		String url = jiraApiAgileBaseUrl + boardResourceUrl;
		String responseBody = executeGetRequest(url);
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
		JsonArray boardsArray = jsonObject.getAsJsonArray("values");

		// Extract boards with type=scrum and project=SIB
		for (JsonElement element : boardsArray) {
			JsonObject board = element.getAsJsonObject();
			String typeFromResponse = board.get("type").getAsString();
			String projectKey = board.getAsJsonObject("location").get("projectKey").getAsString();

			if (type.equalsIgnoreCase(typeFromResponse) && project.equalsIgnoreCase(projectKey)) {
				return gson.toJson(board);
			}
		}
		return responseBody;
	}

	@Override
	public String getSprints(String boardId) {
		String url = jiraApiAgileBaseUrl + boardResourceUrl + "/" + boardId + sprintResourceUrl;
		return executeGetRequest(url);
	}

	private String executeGetRequest(String url) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + jiraToken);
		httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

		try {
			HttpResponse response = httpClient.execute(httpGet);
			String responseBody = EntityUtils.toString(response.getEntity());
			return responseBody;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
	 * Currently this method works with up to 50 sprints. 
	 * TODO consider pagination to process more sprints
	 */
	@Override
	public String getSprints(String boardId, Integer lastCompletedAmount) {
		List<JiraSprint> sublist = getJiraSprints(boardId, lastCompletedAmount);
		return gson.toJson(sublist);
	}

	private List<JiraSprint> getJiraSprints(String boardId, Integer lastCompletedAmount) {
		String jsonResponse = getSprints(boardId);
		List<JiraSprint> jiraClosedSprints = new ArrayList<>();
		JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
		JsonArray valuesArray = jsonObject.getAsJsonArray("values");

		for (JsonElement element : valuesArray) {
			JiraSprint sprint = gson.fromJson(element, JiraSprint.class);
			if (sprint.getState().equals(CLOSED_STATE)) {
				jiraClosedSprints.add(sprint);
			}
			
		}
		Integer amountToSublist = lastCompletedAmount;
		if (lastCompletedAmount > jiraClosedSprints.size()) {
			amountToSublist = jiraClosedSprints.size();
		}
		List<JiraSprint> sublist = jiraClosedSprints
				.subList(jiraClosedSprints.size() - amountToSublist, jiraClosedSprints.size());
		return sublist;
	}

	@Override
	public String getIssuesForSprints(List<String> sprintIds) {
		List<JiraIssue> jiraIssuesList = getJiraIssuesBySprintIds(sprintIds);
		return gson.toJson(jiraIssuesList);
	}

	/*
	 * TODO - consider pagination here, method works with assumption that the total amount of jira issues will be 100
	 * TODO - homework for students - refactor to avoid potential query injection
	 * TODO - homework for students - add exception handling in case Sprint IDs are not valid values
	 */
	private List<JiraIssue> getJiraIssuesBySprintIds(List<String> sprintIds) {
		String requestUrl = jiraApiBaseUrl + searchResourseUrl + "?jql=project=" + jiraProjectName + "%20AND%20Sprint%20in%20(" + String.join(",", sprintIds) + ")" +
				"&maxResults=" + maxResults;
		String responseString = executeGetRequest(requestUrl);

		// Parse JSON response using Gson's JsonParser
		JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
		JsonArray issuesArray = jsonObject.getAsJsonArray("issues");
		List<JiraIssue> jiraIssuesList = new ArrayList<>();
		for (JsonElement issueElement : issuesArray) {
			JsonObject issueObject = issueElement.getAsJsonObject();
			JiraIssue jiraIssue = convertJsonToJiraIssue(issueObject);
			jiraIssuesList.add(jiraIssue);
		}
		return jiraIssuesList;
	}

	@Override
	public Double getAvgVelocity(Integer lastCompletedSprintsAmount) {
		String jsonJiraBoard = getJiraBoard(jiraProjectName, scrumBoardType);
        JsonObject jsonObject = JsonParser.parseString(jsonJiraBoard).getAsJsonObject();
        String jiraBoardId = jsonObject.get("id").getAsString();
		
        List<JiraSprint> jiraSprints = getJiraSprints(jiraBoardId, lastCompletedSprintsAmount);
        List<String> jiraSprintIds = jiraSprints.stream().map(jiraSprint -> jiraSprint.getId().toString()).collect(Collectors.toList());
        
        /*
         * In Jira work items may have multiple sprints assigned. 
         * This can happen when work item is not completed is moved to the following sprint. 
         * That's why in the Jira API work item has array data type to describe sprints. 
         * In order to minimize impact of not correct calculation we put Jira items into HashSet
         * in order to remove duplicates.
         * 
         * Right now we make an assumption that average velocity is verified before the sprint planning, 
         * and the risk of wrong calculation of average velocity is minimal. That's why the method is 
         * implemented in the way like you see below without any complications.
         * 
         * The only risk that may impact average velocity calculation is when ticket was carried over to the 
         * current (not-closed) sprint, and was moved to done. In this case these amounts 
         * of story points may impact total calculation because ticket would still have the reference 
         * to the previous sprint. And there is a way of how to avoid such scenario by implementing additional code. 
         * 
         * TODO - homework for students - retrieve issue details using the Jira REST API, 
         * you will get information about the issue's status, such as "To Do," "In Progress," "Done," etc. 
         * If the status is "Done" and the issue's resolution date (resolutiondate field) falls within the Sprint's 
         * start and end dates, you can infer that the issue was completed within the Sprint. We can add additional check for this.
         */
        Set<JiraIssue> jiraIssuesBySprintIds = new HashSet<>(getJiraIssuesBySprintIds(jiraSprintIds));
        Double totalVelocity = jiraIssuesBySprintIds.stream().filter(jiraIssue -> jiraIssue.getStatus().equalsIgnoreCase(doneStatus))
        	.mapToDouble(jiraIssue -> jiraIssue.getStoryPoints())
        	.sum();
		return totalVelocity / jiraSprintIds.size();
	}
	
}
