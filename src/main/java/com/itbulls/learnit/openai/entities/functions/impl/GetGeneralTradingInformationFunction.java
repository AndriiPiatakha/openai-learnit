package com.itbulls.learnit.openai.entities.functions.impl;

import org.springframework.beans.factory.annotation.Value;

import com.itbulls.learnit.openai.entities.functions.Function;

public class GetGeneralTradingInformationFunction implements Function {
	
	@Value("${gpt.function.revolut.get.trading.fees.info}")
	private String info;

	@Override
	public String execute(String arguments) {
		return info;
	}

}
