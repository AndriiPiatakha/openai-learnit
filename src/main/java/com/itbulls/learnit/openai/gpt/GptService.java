package com.itbulls.learnit.openai.gpt;

import com.itbulls.learnit.openai.entities.GptFunction;

public interface GptService {

	public String getAnswerToSingleQuery(String query, GptFunction... functions);

}
