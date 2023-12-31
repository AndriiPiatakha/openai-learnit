package com.itbulls.learnit.openai;

import static com.itbulls.learnit.openai.entities.ParameterProperties.INTEGER_TYPE;
import static com.itbulls.learnit.openai.entities.ParameterProperties.STRING_TYPE;

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
import com.itbulls.learnit.openai.entities.CreateJiraIssueParameterProperties;
import com.itbulls.learnit.openai.entities.GetAvgVelocityParameterProperties;
import com.itbulls.learnit.openai.entities.GptFunction;
import com.itbulls.learnit.openai.entities.NoProperties;
import com.itbulls.learnit.openai.entities.PlanSprintParameterProperties;
import com.itbulls.learnit.openai.entities.SendEmailParameterProperties;
import com.itbulls.learnit.openai.entities.WeatherParameterProperties;
import com.itbulls.learnit.openai.entities.WeatherParameterProperties.MeasurementUnit;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.entities.functions.impl.CreateJiraIssueFunction;
import com.itbulls.learnit.openai.entities.functions.impl.GetAvgVelocityFunction;
import com.itbulls.learnit.openai.entities.functions.impl.GetJiraIssuesFunction;
import com.itbulls.learnit.openai.entities.functions.impl.GetRisksFunction;
import com.itbulls.learnit.openai.entities.functions.impl.GetWeatherInfoInLocationFunction;
import com.itbulls.learnit.openai.entities.functions.impl.PlanSprintFunction;
import com.itbulls.learnit.openai.entities.functions.impl.SendEmailFunction;
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

	@Bean("gptJiraIssuesFunction")
	public GptFunction gptJiraIssuesFunction(@Value("${gpt.function.jira.get.issues.name}") String functionName,
			@Value("${gpt.function.jira.get.issues.description}") String description) {
		var function = new GptFunction();
		function.setName(functionName);
		function.setDescription(description);
		GptFunction.Parameters parameters = function.new Parameters();
		parameters.setType("object");
		NoProperties properties = new NoProperties();
		parameters.setProperties(properties);
		function.setParameters(parameters);
		return function;
	}

	@Bean("getJiraIssuesFunction")
	public Function getJiraIssuesFunction() {
		return new GetJiraIssuesFunction();
	}

	@Bean("gptCreateJiraIssueFunction")
	public GptFunction gptCreateJiraIssueFunction(@Value("${gpt.function.jira.create.issue.name}") String functionName,
			@Value("${gpt.function.jira.create.issue.description}") String description,
			@Value("${gpt.function.jira.create.issue.attr.assignee.desc}") String assigneeAttrDescription,
			@Value("${gpt.function.jira.create.issue.attr.description.desc}") String descrAttrDescription,
			@Value("${gpt.function.jira.create.issue.attr.issuetype.desc}") String issueTypeAttrDescription,
			@Value("${gpt.function.jira.create.issue.attr.issuetype.epic}") String epicIssueType,
			@Value("${gpt.function.jira.create.issue.attr.issuetype.story}") String storyIssueType,
			@Value("${gpt.function.jira.create.issue.attr.issuetype.task}") String taskIssueType,
			@Value("${gpt.function.jira.create.issue.attr.issuetype.bug}") String bugIssueType,
			@Value("${gpt.function.jira.create.issue.attr.duedate.desc}") String dueDateAttrDescription,
			@Value("${gpt.function.jira.create.issue.attr.summary.format}") String dueDateFormat,
			@Value("${gpt.function.jira.create.issue.attr.summary.desc}") String summaryAttrDescription) {
		var function = new GptFunction();
		function.setName(functionName);
		function.setDescription(description);
		GptFunction.Parameters parameters = function.new Parameters();
		parameters.setType("object");

		CreateJiraIssueParameterProperties properties = new CreateJiraIssueParameterProperties();
		properties.setAssignee(properties.new Assignee(STRING_TYPE, assigneeAttrDescription));
		properties.setDescription(properties.new Description(STRING_TYPE, descrAttrDescription));
		properties.setIssueType(properties.new IssueType(STRING_TYPE, issueTypeAttrDescription,
				new String[] { epicIssueType, storyIssueType, taskIssueType, bugIssueType }));
		properties.setDueDate(properties.new DueDate(STRING_TYPE, dueDateAttrDescription, dueDateFormat));
		properties.setSummary(properties.new Summary(STRING_TYPE, summaryAttrDescription));

		parameters.setProperties(properties);
		parameters.setRequired(new String[] { "summary", "description", "issue_type", "due_date" });
		function.setParameters(parameters);
		return function;
	}

	@Bean("createJiraIssueFunction")
	public Function getCreateJiraIssueFunction() {
		return new CreateJiraIssueFunction();
	}

	@Bean("gptSendEmailFunction")
	public GptFunction gptSendEmailFunction(@Value("${gpt.function.gmail.send.email.name}") String functionName,
			@Value("${gpt.function.gmail.send.email.name}") String description, 
			@Value("${gpt.function.gmail.send.email.attr.addressee.email.desc}") String addresseeEmailAttrDescription, 
			@Value("${gpt.function.gmail.send.email.attr.addressee.name.desc}") String addresseeNameAttrDescription, 
			@Value("${gpt.function.gmail.send.email.attr.content.desc}") String contentAttrDescription,
			@Value("${gpt.function.gmail.send.email.attr.subject.desc}") String subjectAttrDescription) {
		var function = new GptFunction();
		function.setName(functionName);
		function.setDescription(description);
		GptFunction.Parameters parameters = function.new Parameters();
		parameters.setType("object");

		SendEmailParameterProperties properties = new SendEmailParameterProperties();
		properties.setAddresseeEmail(properties.new AddresseeEmail(STRING_TYPE, addresseeEmailAttrDescription));
		properties.setAddresseeName(properties.new AddresseeName(STRING_TYPE, addresseeNameAttrDescription));
		properties.setContent(properties.new Content(STRING_TYPE, contentAttrDescription));
		properties.setSubject(properties.new Subject(STRING_TYPE, subjectAttrDescription));

		parameters.setProperties(properties);
		parameters.setRequired(new String[] {"content", "subject"});
		function.setParameters(parameters);
		return function;
	}
	
	@Bean("sendEmailFunction")
	public Function sendEmailFunction() {
		return new SendEmailFunction();
	}
	
	@Bean("gptGetAvgVelocityFunction")
	public GptFunction gptGetAverageVelocityFunction(@Value("${gpt.function.jira.avg.velocity.name}") String functionName,
			@Value("${gpt.function.jira.avg.velocity.description}") String description, 
			@Value("${gpt.function.jira.avg.velocity.attr.amount.sprints.desc}") String amountOfCompletedSprintsAttrDescription) {
		var function = new GptFunction();
		function.setName(functionName);
		function.setDescription(description);
		GptFunction.Parameters parameters = function.new Parameters();
		parameters.setType("object");
		
		GetAvgVelocityParameterProperties properties = new GetAvgVelocityParameterProperties();
		properties.setAmountOfCompletedSprints(properties.new AmountOfCompletedSprints(INTEGER_TYPE, amountOfCompletedSprintsAttrDescription));
		parameters.setProperties(properties);
		parameters.setRequired(new String[] {"amount_of_completed_sprints"});
		function.setParameters(parameters);
		return function;
	}
	
	@Bean("getAvgVelocityFunction")
	public Function getAvgVelocityFunction() {
		return new GetAvgVelocityFunction();
	}
	
	@Bean("gptPlanSprintFunction")
	public GptFunction planSprint(@Value("${gpt.function.jira.plan.sprint.name}") String functionName,
			@Value("${gpt.function.jira.plan.sprint.description}") String description, 
			@Value("${gpt.function.jira.plan.sprint.attr.sprint.name.desc}") String sprintNameAttrDescription,
			@Value("${gpt.function.jira.plan.sprint.attr.capacity.desc}") String capacityAttrDescription) {
		var function = new GptFunction();
		function.setName(functionName);
		function.setDescription(description);
		GptFunction.Parameters parameters = function.new Parameters();
		parameters.setType("object");
		
		PlanSprintParameterProperties properties = new PlanSprintParameterProperties();
		properties.setSprintName(properties.new SprintName(STRING_TYPE, sprintNameAttrDescription));
		properties.setCapacity(properties.new Capacity(INTEGER_TYPE, capacityAttrDescription));
		
		parameters.setProperties(properties);
		parameters.setRequired(new String[] {"sprint_name", "capacity"});
		function.setParameters(parameters);
		return function;
	}
	
	@Bean("planSprintFunction")
	public Function planSprintFunction() {
		return new PlanSprintFunction();
	}
	
	@Bean("gptRisksFunction")
	public GptFunction gptRisksFunction(@Value("${gpt.function.jira.get.risks.name}") String functionName,
			@Value("${gpt.function.jira.get.risks.description}") String description) {
		var function = new GptFunction();
		function.setName(functionName);
		function.setDescription(description);
		GptFunction.Parameters parameters = function.new Parameters();
		parameters.setType("object");
		NoProperties properties = new NoProperties();
		parameters.setProperties(properties);
		function.setParameters(parameters);
		return function;
	}

	@Bean("getRisksFunction")
	public Function getRisksFunction() {
		return new GetRisksFunction();
	}
}
