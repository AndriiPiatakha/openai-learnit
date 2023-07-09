package com.itbulls.learnit.openai;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itbulls.learnit.openai.context.SlackTeamContext;
import com.itbulls.learnit.openai.entities.GptFunction;
import com.itbulls.learnit.openai.entities.WeatherParameterProperties;
import com.itbulls.learnit.openai.entities.WeatherParameterProperties.MeasurementUnit;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.entities.functions.impl.GetWeatherInfoInLocationFunction;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;

@Configuration
public class BeansConfiguration {

	@Value("${slack.security.token.bot}")
	private String slackSecurityTokenBot;
	@Value("${slack.security.token.user}")
	private String slackSecurityTokenUser;

	@Bean
	public Gson gson() {
		/*
		 * Need to set field naming policy in order to convert attribute names from and
		 * to Java-style. For example: presencePenalty ==> presence_penalty
		 * completion_tokens ==> completionTokens finish_reason ==> finishReason
		 */

		return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	@Bean
	public HttpClient httpClient() {
		return HttpClientBuilder.create().build();
	}

	@Bean("getWeatherInfoInLocationFunction")
	public Function weatherFunctionCall() {
		return new GetWeatherInfoInLocationFunction();
	}

	@Bean("gptWeatherFunction")
	public GptFunction gptWeatherInfoFunction() {
		var function = new GptFunction();
		function.setName("getWeatherInfoInLocationFunction");
		function.setDescription("Get the current weather in a given location");
		GptFunction.Parameters parameters = function.new Parameters();
		parameters.setType("object");
		parameters.setRequired(new String[] { "location" });
		WeatherParameterProperties properties = new WeatherParameterProperties();
		properties.setLocation(properties.new Location("string", "The city and state, e.g. Vienna, Austria"));
		properties.setUnit(properties.new MeasurementUnit("string",
				"One out of two possible values: celsius or fahrenheit. Temperature measurement unit",
				new String[] { MeasurementUnit.CELSIUS, MeasurementUnit.FAHRENHEIT }));
		parameters.setProperties(properties);
		function.setParameters(parameters);
		return function;
	}

	@Bean("slackBotClient")
	public MethodsClient slackMethodsClientBot() {
		return Slack.getInstance().methods(slackSecurityTokenBot);
	}
	
	@Bean("slackUserClient")
	public MethodsClient slackMethodsClientUser() {
		return Slack.getInstance().methods(slackSecurityTokenUser);
	}
	
	@Bean("slackContextMap")
	public Map<String, SlackTeamContext> slackContextMap() {
		return new HashMap<>();
	}

}
