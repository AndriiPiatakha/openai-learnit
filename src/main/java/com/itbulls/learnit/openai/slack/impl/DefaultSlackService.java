package com.itbulls.learnit.openai.slack.impl;

import static com.itbulls.learnit.openai.entities.GptMessage.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itbulls.learnit.openai.context.SlackTeamContext;
import com.itbulls.learnit.openai.entities.GptFunction;
import com.itbulls.learnit.openai.entities.GptMessage;
import com.itbulls.learnit.openai.entities.SlackRequestData;
import com.itbulls.learnit.openai.gpt.GptService;
import com.itbulls.learnit.openai.slack.SlackService;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatDeleteRequest;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.request.conversations.ConversationsListRequest;
import com.slack.api.methods.request.users.UsersListRequest;
import com.slack.api.methods.response.chat.ChatDeleteResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.methods.response.users.UsersListResponse;
import com.slack.api.model.Conversation;
import com.slack.api.model.Message;
import com.slack.api.model.User;

@Service
public class DefaultSlackService implements SlackService {

	private static final String MESSAGE_TYPE = "message";
	private static final String CHANNEL_JOIN_SUBTYPE = "channel_join";
	
	@Autowired
	private Gson gson;
	@Autowired
	private GptService gptService;
	@Autowired
	@Qualifier("slackBotClient")
	private MethodsClient slackBotClient;
	@Autowired
	@Qualifier("slackUserClient")
	private MethodsClient slackUserClient;
	@Autowired
	@Qualifier("slackContextMap")
	private Map<String, SlackTeamContext> slackContextMap;
	@Autowired
	private List<GptFunction> functions;
	
	@Value("${slack.max.messages.from.history}")
	private Integer maxMessagesSlackHistory;
	@Value("${slack.wait.seconds.for.retry}")
	private Integer waitForRetrySlack;
	@Value("${slack.retry.attempts}")
	private Integer retryAttempts;
	@Value("${slack.bot.name}")
	private Object botAppName;
	@Value("${gpt.chat.characters.amount.max}")
	private Integer maxCharacters;
	@Value("${slack.max.messages.to.remove}")
	private Integer maxLimitToRemove;
	@Value("${jira.api.projet.name}")
	private String jiraProjectName;
	@Value("${jira.project.url}")
	private String jiraProjectUrl;
	@Value("${jira.browse.url}")
	private String browseUrl;

	public void processOnMentionEvent(String requestBody) {
		SlackRequestData requestData = extractSlackRequestData(requestBody);
		List<GptMessage> contextMessages = extractContextForSlackRequest(requestData);
		String gptResponseString = gptService.getAnswerToSingleQuery(contextMessages, functions.toArray(GptFunction[]::new));
		gptResponseString = addHyperReferencesToJira(gptResponseString);
		sendMessageToSlack(gptResponseString, requestData.getChannelIdFrom());
	}

	private String addHyperReferencesToJira(String gptResponseString) {
		Pattern jiraPattern = Pattern.compile("\\b(" + jiraProjectName + "-\\d+)\\b");
		String jiraBaseUrl = jiraProjectUrl + browseUrl + "/";
		StringBuilder result = new StringBuilder();
        Matcher matcher = jiraPattern.matcher(gptResponseString);

        while (matcher.find()) {
//            String ticketId = matcher.group();
//            String hyperlink = String.format("(%s%s)", jiraBaseUrl, ticketId);
//            matcher.appendReplacement(result, Matcher.quoteReplacement(hyperlink));
            
            String ticketId = matcher.group(1);
            String hyperlink = String.format("(%s%s)", jiraBaseUrl, ticketId);
            matcher.appendReplacement(result, "$0 " + hyperlink);
        }
        matcher.appendTail(result);
        return result.toString();
	}

	private List<GptMessage> extractContextForSlackRequest(SlackRequestData requestData) {
		List<GptMessage> gptMessages = new ArrayList<>();
		
		try {
			ConversationsHistoryResponse historyResponse = slackBotClient.conversationsHistory(r -> r
			        .channel(requestData.getChannelIdFrom())
			        .limit(maxMessagesSlackHistory));
			
			List<Message> slackMessages = historyResponse.getMessages(); // the latest messages at the beginning
			SlackTeamContext slackTeamContext = slackContextMap.get(slackMessages.get(0).getTeam());
			if (slackTeamContext == null) {
				return gptMessages;
			}
			Map<String, String> userIdToNameMap = slackTeamContext.getUserIdToNameMap();
			
			int totalContextLength = 0;
			for (Message message : slackMessages) {
				if (message.getType().equals(MESSAGE_TYPE) && (message.getSubtype() == null 
						|| !message.getSubtype().equals(CHANNEL_JOIN_SUBTYPE))) {
				    String messageWithoutUserIds = substituteUserIdsWithNames(message.getText(), userIdToNameMap);
				    int length = messageWithoutUserIds.length();
				    if (totalContextLength + length > maxCharacters) {
				    	break;
				    } else {
						totalContextLength += length;
				    }
				    
				    String authorName = userIdToNameMap.get(message.getUser());
				    if (authorName == null) {
				    	authorName = message.getUser();
				    } else {
				    	authorName = authorName.replaceAll("\\s+", "_");
				    }
				    
				    if (authorName.equals(botAppName)) {
				    	gptMessages.add(new GptMessage(ASSISTANT_ROLE, messageWithoutUserIds, authorName));
				    } else {
				    	gptMessages.add(new GptMessage(USER_ROLE, messageWithoutUserIds, authorName));
				    }
				}
			    
			}
			Collections.reverse(gptMessages); // to make the latest messages at the end
		} catch (IOException | SlackApiException e) {
			e.printStackTrace();
		} 
		return gptMessages;
	}

	private void sendMessageToSlack(String gptResponseString, String channelId) {
		ChatPostMessageRequest request = ChatPostMessageRequest
				.builder()
				.channel(channelId)
				.text(gptResponseString)
				.build();
		try {
			slackBotClient.chatPostMessage(request);
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
		String teamId = jsonObject.get("team_id").getAsString();
		Map<String, String> userIdToNameMap = null;
		for (int i = 0; i < retryAttempts; i++) {
			try {
				userIdToNameMap = extractUserIdToNameMap(teamId);
				break;
			} catch (SlackApiException e) {
				e.printStackTrace();
				try {
					TimeUnit.SECONDS.sleep(waitForRetrySlack);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
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

	private Map<String, String> extractUserIdToNameMap(String teamId) throws SlackApiException {
		SlackTeamContext slackTeamContext = slackContextMap.get(teamId);
		try {
			UsersListRequest usersListRequest = UsersListRequest.builder().build();
			UsersListResponse usersListResponse = slackBotClient.usersList(usersListRequest);
			Map<String, String> resultMap = new HashMap<>();
			for (User user : usersListResponse.getMembers()) {
				resultMap.put(user.getId(), user.getRealName());
			}
			if (slackTeamContext == null) {
				slackContextMap.put(teamId, new SlackTeamContext(resultMap));
			} else {
				slackTeamContext.setUserIdToNameMap(resultMap);
			}
			return resultMap;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SlackApiException e) {
			e.printStackTrace();
			if (slackTeamContext != null && 
					slackTeamContext.getUserIdToNameMap() != null) {
				return slackTeamContext.getUserIdToNameMap();
			} else {
				throw e;
			}
		}
		return Collections.EMPTY_MAP;
	}

	@Override
	public void removeAllMessagesFromChannel(String channelId) {
		try {
			ConversationsListResponse conversationsListResponse = slackUserClient
					.conversationsList(ConversationsListRequest.builder().build());
			List<Conversation> conversations = conversationsListResponse.getChannels();

			Conversation targetConversation = conversations.stream()
					.filter(conversation -> conversation.getId().equals(channelId)).findFirst().orElse(null);
			if (targetConversation != null) {
				ConversationsHistoryResponse historyResponse = slackUserClient.conversationsHistory(
						ConversationsHistoryRequest.builder().channel(targetConversation.getId()).limit(maxLimitToRemove).build());
				List<Message> messages = historyResponse.getMessages();
				for (Message message : messages) {
					
					for (int i = 0; i < retryAttempts; i++) {
						try {
							ChatDeleteResponse response = slackUserClient.chatDelete(
				                    ChatDeleteRequest.builder()
				                            .channel(channelId)
				                            .asUser(true)
				                            .ts(message.getTs())
				                            .build()
				            );
							break;
						} catch (SlackApiException e) { 
							e.printStackTrace();
							try {
								TimeUnit.SECONDS.sleep(waitForRetrySlack);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
