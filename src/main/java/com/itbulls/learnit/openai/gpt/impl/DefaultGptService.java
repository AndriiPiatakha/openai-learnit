package com.itbulls.learnit.openai.gpt.impl;
import static com.itbulls.learnit.openai.entities.GptMessage.USER_ROLE;

import java.io.IOException;
import java.util.ArrayList;
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
import com.itbulls.learnit.openai.entities.GptMessage;
import com.itbulls.learnit.openai.entities.GptRequest;
import com.itbulls.learnit.openai.entities.GptResponse;
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
	
	@Override
	public String getAnswerToSingleQuery(String query) {
		var request = new GptRequest();
		request.setModel(model);
		List<GptMessage> messages = new ArrayList<>();
//		messages.add(new GptMessage(SYSTEM_ROLE, "You are a helpful assistant."));
		messages.add(new GptMessage(USER_ROLE, query));
		request.setMessages(messages);
		request.setTemperature(temperature);
		request.setPresencePenalty(presencePenalty);
		request.setMaxTokens(maxTokens);
		String requestBody = gson.toJson(request);
		return getResponseFromGpt(requestBody);
	}
	
	private String getResponseFromGpt(String requestBody) {
		
		String authorizationHeader = "Bearer " + chatGptApiKey;
		String contentTypeHeader = "application/json";
		HttpPost request = new HttpPost(chatGptUrl);
		request.setHeader(HttpHeaders.AUTHORIZATION, authorizationHeader);
		request.setHeader(HttpHeaders.CONTENT_TYPE, contentTypeHeader);
		request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
		String response = "";
		for (int i = 0; i < retryAttempts; i++) {
			try {
				response = extractGptResponseContent(request);
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
	
	private String extractGptResponseContent(HttpPost request) throws IOException, ClientProtocolException {
		HttpResponse response = httpClient.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String responseBody = EntityUtils.toString(entity);
			if (statusCode == HttpStatus.SC_BAD_REQUEST) {
				return responseBody;
			}
			GptResponse gptResponse = gson.fromJson(responseBody, GptResponse.class);
			return gptResponse.getChoices().get(0).getMessage().getContent();
		}
		return "";
	}

}
