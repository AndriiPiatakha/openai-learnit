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
		HttpPost httpRequest = new HttpPost(chatGptUrl);
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
				gptRequest.getMessages().add(gptMessage);
				
				// Functions submitted also counted as prompt tokens
				// to optimze tokens usage - I don't send functions list the second time
				gptRequest.setFunctions(null);
				return getResponseFromGpt(gptRequest);
			}
			
			return message.getContent();
		}
		return "";
	}

}
