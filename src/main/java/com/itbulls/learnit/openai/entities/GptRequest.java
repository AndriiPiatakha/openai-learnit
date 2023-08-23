package com.itbulls.learnit.openai.entities;

import java.util.List;
import java.util.Map;

public class GptRequest {
	
	private String model;
	private List<GptMessage> messages;
	private Integer n;
	private Double temperature;
	private Integer maxTokens;
	private boolean stream;
	private Double presencePenalty;
	private Double frequencyPenalty;
	private Double topP;
	private String stop;
	private Map<String, Integer> logitBias;
	private String user;
	private List<GptFunction> functions;
	private String prompt;
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public List<GptMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<GptMessage> messages) {
		this.messages = messages;
	}
	public Integer getN() {
		return n;
	}
	public void setN(Integer n) {
		this.n = n;
	}
	public Double getTemperature() {
		return temperature;
	}
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	public Integer getMaxTokens() {
		return maxTokens;
	}
	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}
	public boolean isStream() {
		return stream;
	}
	public void setStream(boolean stream) {
		this.stream = stream;
	}
	public Double getPresencePenalty() {
		return presencePenalty;
	}
	public void setPresencePenalty(Double presencePenalty) {
		this.presencePenalty = presencePenalty;
	}
	public Double getFrequencyPenalty() {
		return frequencyPenalty;
	}
	public void setFrequencyPenalty(Double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}
	public Double getTopP() {
		return topP;
	}
	public void setTopP(Double topP) {
		this.topP = topP;
	}
	public String getStop() {
		return stop;
	}
	public void setStop(String stop) {
		this.stop = stop;
	}
	public Map<String, Integer> getLogitBias() {
		return logitBias;
	}
	public void setLogitBias(Map<String, Integer> logitBias) {
		this.logitBias = logitBias;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public List<GptFunction> getFunctions() {
		return functions;
	}
	public void setFunctions(List<GptFunction> functions) {
		this.functions = functions;
	}
	
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
}
