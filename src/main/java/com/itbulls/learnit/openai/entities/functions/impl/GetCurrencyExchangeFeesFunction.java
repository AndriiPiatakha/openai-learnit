package com.itbulls.learnit.openai.entities.functions.impl;

import org.springframework.beans.factory.annotation.Value;

import com.itbulls.learnit.openai.entities.functions.Function;

public class GetCurrencyExchangeFeesFunction implements Function {
	
	@Value("${gpt.function.revolut.get.currency.exchange.fees.info}")
	private String currencyExchangeFeesInfo;

	@Override
	public String execute(String arguments) {
		return currencyExchangeFeesInfo;
	}

}
