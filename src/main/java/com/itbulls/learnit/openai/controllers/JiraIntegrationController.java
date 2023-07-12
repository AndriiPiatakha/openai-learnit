package com.itbulls.learnit.openai.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class JiraIntegrationController {
	
	@Value("${jira.api.base.url}")
	private String jiraApiBaseUrl;
	@Value("${jira.api.resourse.issue}")
	private String issueResourseUrl;
	@Value("${jira.token}")
	private String jiraToken;
	
	@Autowired
	private HttpClient httpClient;
	

	@GetMapping("/v1/jira")
	public String getIssueById(@RequestParam String issueId) {
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
