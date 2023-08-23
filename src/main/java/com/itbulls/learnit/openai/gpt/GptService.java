package com.itbulls.learnit.openai.gpt;

import java.util.List;

import com.itbulls.learnit.openai.entities.GptFunction;
import com.itbulls.learnit.openai.entities.GptMessage;

public interface GptService {

	public String getAnswerToSingleQuery(String query, GptFunction... functions);

	public String getAnswerToSingleQuery(String query, String userName, GptFunction... functions);
	
	/**
	 * 
	 * @param context - list of messages in conversation. The first message is the oldest one. 
	 * The last message is the latest one.
	 * @param functions
	 * @return answer from GPT
	 */
	public String getAnswerToSingleQuery(List<GptMessage> context, GptFunction... functions);
	
	public String getAnswerToSingleQuery(String query, String modelName);

}
