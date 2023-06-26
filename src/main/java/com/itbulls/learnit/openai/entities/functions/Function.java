package com.itbulls.learnit.openai.entities.functions;

public interface Function {

	/**
	 * @param arguments
	 * @return JSON of response from function
	 */
	String execute(String arguments);

}
