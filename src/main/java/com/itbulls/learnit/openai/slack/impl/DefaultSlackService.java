package com.itbulls.learnit.openai.slack.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itbulls.learnit.openai.entities.SlackRequestData;
import com.itbulls.learnit.openai.gpt.GptService;
import com.itbulls.learnit.openai.slack.SlackService;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.users.UsersListRequest;
import com.slack.api.methods.response.users.UsersListResponse;
import com.slack.api.model.User;

@Service
public class DefaultSlackService implements SlackService {

	@Autowired
	private Gson gson;
	@Autowired
	private GptService gptService;
	@Autowired
	private MethodsClient slackMethodsClient;

	public void processOnMentionEvent(String requestBody) {
		SlackRequestData requestData = extractSlackRequestData(requestBody);
		String gptResponseString = gptService.getAnswerToSingleQuery(requestData.getMessage(),
				requestData.getAuthorName());
		sendMessageToSlack(gptResponseString, requestData.getChannelIdFrom());
	}

	private void sendMessageToSlack(String gptResponseString, String channelId) {
		ChatPostMessageRequest request = 
				ChatPostMessageRequest.builder()
					.channel(channelId)
					.text(gptResponseString)
					.build();
		try {
			slackMethodsClient.chatPostMessage(request);
		} catch (IOException | SlackApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param jsonObject - response from Slack
	 * @return response is an array, where element with index 0 is the name of the
	 *         author, and element with index 1 is a text of request,
	 */
	private SlackRequestData extractSlackRequestData(String requestBody) {
		JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);
		JsonObject eventJsonObject = jsonObject.getAsJsonObject("event");
		String message = eventJsonObject.get("text").getAsString();
		String messageAuthorId = eventJsonObject.get("user").getAsString();
		String channelId = eventJsonObject.get("channel").getAsString();

		Map<String, String> userIdToNameMap = extractUserIdToNameMap();
		String messageAuthorName = userIdToNameMap.get(messageAuthorId);
		message = substituteUserIdsWithNames(message, userIdToNameMap);
		return new SlackRequestData(messageAuthorName, message, channelId);
	}

	public String substituteUserIdsWithNames(String message, Map<String, String> userIdToNameMap) {
		String[] userIds = extractUserIds(message);
		StringBuilder stringBuilder = new StringBuilder(message);
		for (String userId : userIds) {
			String mention = "<@" + userId + ">";
			String name = userIdToNameMap.get(userId);
			if (name != null) {
				int startIndex = stringBuilder.indexOf(mention);
				int endIndex = startIndex + mention.length();
				stringBuilder.replace(startIndex, endIndex, name);
			}
		}

		return stringBuilder.toString();
	}

	private String[] extractUserIds(String input) {
		List<String> userIds = new ArrayList<>();
		Pattern pattern = Pattern.compile("<@(\\w+)>");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			userIds.add(matcher.group(1));
		}
		return userIds.toArray(new String[userIds.size()]);
	}

	private Map<String, String> extractUserIdToNameMap() {
		try {
			UsersListRequest usersListRequest = UsersListRequest.builder().build();
			UsersListResponse usersListResponse = slackMethodsClient.usersList(usersListRequest);
			Map<String, String> resultMap = new HashMap<>();
			for (User user : usersListResponse.getMembers()) {
				resultMap.put(user.getId(), user.getRealName());
			}
			return resultMap;
		} catch (IOException | SlackApiException e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_MAP;
	}
}
