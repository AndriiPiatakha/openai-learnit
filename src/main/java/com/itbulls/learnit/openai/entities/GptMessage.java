package com.itbulls.learnit.openai.entities;

import java.util.Map;

public class GptMessage {

	public static final String SYSTEM_ROLE = "system";
	public static final String ASSISTANT_ROLE = "assistant";
	public static final String USER_ROLE = "user";
	public static final String FUNCTION_ROLE = "function";

	private String role;
	private String content;
	private String name;
	private FunctionCall functionCall;

	public FunctionCall getFunctionCall() {
		return functionCall;
	}

	public void setFunctionCall(FunctionCall functionCall) {
		this.functionCall = functionCall;
	}

	public GptMessage() {
	}

	public GptMessage(String role, String content) {
		this.role = role;
		this.content = content;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public class FunctionCall {

		private String name;
		private String arguments;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getArguments() {
			return arguments;
		}

		public void setArguments(String arguments) {
			this.arguments = arguments;
		}

		@Override
		public String toString() {
			return "FunctionCall [name=" + name + ", arguments=" + arguments + "]";
		}
	}

	@Override
	public String toString() {
		return "GptMessage [role=" + role + ", content=" + content + ", name=" + name + ", functionCall=" + functionCall
				+ "]";
	}

}
