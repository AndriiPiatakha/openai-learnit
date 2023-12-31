package com.itbulls.learnit.openai.gpt.impl;
import static com.itbulls.learnit.openai.entities.GptMessage.FUNCTION_ROLE;
import static com.itbulls.learnit.openai.entities.GptMessage.USER_ROLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.entities.GptFunction;
import com.itbulls.learnit.openai.entities.GptMessage;
import com.itbulls.learnit.openai.entities.GptMessage.FunctionCall;
import com.itbulls.learnit.openai.entities.GptRequest;
import com.itbulls.learnit.openai.entities.GptResponse;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.entities.functions.FunctionFactory;
import com.itbulls.learnit.openai.gpt.GptService;

@Service
public class DefaultGptService implements GptService {

	@Value("${gpt.chat.model}")
	private String model;
	@Value("${gpt.chat.temperature}")
	private Double temperature;
	@Value("${gpt.chat.presence.penalty}")
	private Double presencePenalty;
	@Value("${gpt.chat.completion.maxtokens}")
	private Integer maxTokens;
	@Value("${gpt.api.key}")
	private String chatGptApiKey;
	@Value("${gpt.chat.api.url}")
	private String chatGptUrl;
	@Value("${gpt.chat.sendrequest.retryattempts}")
	private Integer retryAttempts;
	@Value("${gpt.chat.sendrequest.waitforretry.seconds}")
	private Integer waitSeconds;
	@Value("${gpt.chat.characters.amount.max}")
	private Integer maxCharacters;
	@Value("${gpt.chat.characters.not.enough.context.limit}")
	private String notEnoughContextLimitToProcessThisQueryMsg;
	@Value("${gpt.confluence.bot.stop.completions.separator}")
	private String stopCompletionSeparator;
	@Value("${gpt.confluence.bot.prompt.separator}")
	private String promptSeparator;
	@Value("${gpt.completions.api.url}")
	private String completionsGptUrl;
	@Value("${gpt.confluence.bot.enabled}")
	private boolean isConfluenceBotEnabled;
	
	@Autowired
	private Gson gson;
	@Autowired
	private HttpClient httpClient;
	@Autowired
	private FunctionFactory functionFactory;
	
	
	@Override
	public String getAnswerToSingleQuery(String query, GptFunction... gptFunctions) {
		List<GptMessage> messages = new ArrayList<>();
//		messages.add(new GptMessage(SYSTEM_ROLE, "You are a helpful assistant."));
		messages.add(new GptMessage(USER_ROLE, query));
		var request = prepareRequest(messages, gptFunctions);
		return getResponseFromGpt(request);
	}
	
	
	@Override
	public String getAnswerToSingleQuery(String query, String userName, GptFunction... gptFunctions) {
		List<GptMessage> messages = new ArrayList<>();
		if (userName != null) {
			userName = userName.replaceAll("\\s+", "_");
		}
		messages.add(new GptMessage(USER_ROLE, query, userName));
		var request = prepareRequest(messages, gptFunctions);
		return getResponseFromGpt(request);
	}
	

	@Override
	public String getAnswerToSingleQuery(List<GptMessage> context, GptFunction... gptFunctions) {
		var request = prepareRequest(context, gptFunctions);
		return getResponseFromGpt(request);
	}
	

	@Override
	public String getAnswerToSingleQuery(String prompt, String modelName) {
		var request = prepareCompletionsRequest(prompt, modelName);
		return getResponseFromGpt(request);
	}
	
	private GptRequest prepareCompletionsRequest(String prompt, String modelName) {
		var request = new GptRequest();
		request.setPrompt(prompt + promptSeparator);
		request.setModel(modelName);
		request.setStop(stopCompletionSeparator);
		request.setMaxTokens(maxTokens);
		return request;
	}


	private GptRequest prepareRequest(List<GptMessage> messages, GptFunction... gptFunctions) {
		var request = new GptRequest();
		request.setModel(model);
		request.setMessages(messages);
		request.setTemperature(temperature);
		request.setPresencePenalty(presencePenalty);
		request.setMaxTokens(maxTokens);
		if (gptFunctions != null && gptFunctions.length > 0) {
			List<GptFunction> functions = Arrays.asList(gptFunctions);
			request.setFunctions(functions);
		}
		return request;
	}
	
	private String getResponseFromGpt(GptRequest gptRequest) {
		HttpPost httpRequest = prepareHttpRequest(gptRequest);
		String response = "";
		for (int i = 0; i < retryAttempts; i++) {
			try {
				response = extractGptResponseContent(httpRequest, gptRequest);
				return response;
			} catch (IOException | RuntimeException e) { // This will capture also java.net.SocketException
				e.printStackTrace();
				try {
					TimeUnit.SECONDS.sleep(waitSeconds);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return response;
	}
	

	private HttpPost prepareHttpRequest(GptRequest gptRequest) {
		String requestBody = gson.toJson(gptRequest);
		String authorizationHeader = "Bearer " + chatGptApiKey;
		String contentTypeHeader = "application/json";
		HttpPost httpRequest = null;
		if (isConfluenceBotEnabled) {
			httpRequest = new HttpPost(completionsGptUrl);
		} else {
			httpRequest = new HttpPost(chatGptUrl);
		}
		
		httpRequest.setHeader(HttpHeaders.AUTHORIZATION, authorizationHeader);
		httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, contentTypeHeader);
		httpRequest.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
		return httpRequest;
	}
	
	private String extractGptResponseContent(HttpPost request, GptRequest gptRequest) throws IOException, ClientProtocolException {
		HttpResponse response = httpClient.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		
		if (entity != null) {
			String responseBody = EntityUtils.toString(entity);
			if (statusCode == HttpStatus.SC_BAD_REQUEST) {
				return responseBody;
			}
			GptResponse gptResponse = gson.fromJson(responseBody, GptResponse.class);
			if (gptResponse.getChoices() == null) { // this means there are no answers at all
				return responseBody;
			}
			GptMessage message = gptResponse.getChoices().get(0).getMessage();
			
			if (message == null) { // this means we are dealing with Completions API
				return gptResponse.getChoices().get(0).getText();
			}
			
			// optionally, we can check "finish_reason" of Choice object
			// In case of function call request, the finish reason is "function_call"
			FunctionCall functionCall = message.getFunctionCall();
			if (functionCall != null) {
				Function function = functionFactory.getFunctionByName(functionCall.getName());
				String functionResponse = function.execute(functionCall.getArguments());
				var gptMessage = new GptMessage();
				gptMessage.setRole(FUNCTION_ROLE);
				gptMessage.setName(functionCall.getName());
				gptMessage.setContent(functionResponse);
				
				addMessageWithTokenLimit(gptRequest, gptMessage);
				
				// Functions submitted also counted as prompt tokens
				// to optimze tokens usage - I don't send functions list the second time
				gptRequest.setFunctions(null);
				
				/*
				 * This check we need for the following scenario:
				 * - Imagine that Jira response contains information about thousands of tickets, with long summary and descriptions.
				 * According to the logic of addMessageWithTokenLimit() method, theoretically, we can remove all messages from the context,
				 * and even not being able to add response from Jira because of the respect of token limit. Anyway, there is no sense to add 
				 * function call response to the context if all context is removed, because in this case GPT will not be able to provide us 
				 * with the answer, because original request was lost. 
				 * 
				 * Different workarounds can be applied here, but one of the most straightforward is to tell the end user that 
				 * there is not enough context limit to process the request.
				 */
				if (gptRequest.getMessages().size() == 0) {
					return notEnoughContextLimitToProcessThisQueryMsg;
				}
				return getResponseFromGpt(gptRequest);
			}
			
			return message.getContent();
		}
		return "";
	}


	private void addMessageWithTokenLimit(GptRequest gptRequest, GptMessage gptMessage) {
		List<GptMessage> contextMessages = gptRequest.getMessages();
		if (contextMessages.size() == 0) {
			return;
		}
		int contextLength = contextMessages.stream().mapToInt(m -> m.getContent().length()).sum();
		if ( (contextLength + gptMessage.getContent().length()) < maxCharacters ) {
			contextMessages.add(gptMessage);
		} else {
			contextMessages.remove(0); // remove the oldest message, because oldest are at the beginning of the list
			addMessageWithTokenLimit(gptRequest, gptMessage);
		}
	}


}
