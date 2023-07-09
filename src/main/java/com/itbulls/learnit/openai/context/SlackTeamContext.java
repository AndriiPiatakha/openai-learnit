package com.itbulls.learnit.openai.context;

import java.util.Map;

public class SlackTeamContext {
	
	private Map<String, String> userIdToNameMap;

	public SlackTeamContext(Map<String, String> userIdToNameMap) {
		this.userIdToNameMap = userIdToNameMap;
	}

	public Map<String, String> getUserIdToNameMap() {
		return userIdToNameMap;
	}

	public void setUserIdToNameMap(Map<String, String> userIdToNameMap) {
		this.userIdToNameMap = userIdToNameMap;
	}
}
