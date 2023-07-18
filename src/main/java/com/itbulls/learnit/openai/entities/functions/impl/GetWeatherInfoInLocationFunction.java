package com.itbulls.learnit.openai.entities.functions.impl;

import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itbulls.learnit.openai.entities.WeatherInfo;
import com.itbulls.learnit.openai.entities.functions.Function;

public class GetWeatherInfoInLocationFunction implements Function {

	@Autowired
	private Gson gson;
	
	
	/* 
	 * Example of response from GPT
	 * "function_call": {
          "name": "getWeatherInfoInLocationFunction",
          "arguments": "{\n\"location\": \"London\",\n\"unit\": \"fahrenheit\"\n}"
        }
	 * 
	 */
	
	@Override
	public String execute(String arguments) {
		Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> argumentsMap = gson.fromJson(arguments, type);
        String location = argumentsMap.get("location");
        String unit = argumentsMap.get("unit");
		
		WeatherInfo weatherInfo = getWeatherInfo(location, unit);
		return gson.toJson(weatherInfo);
	}
	
	/*
	 * Here is an example of function. It is hard coded to return always the same weather
	 * But in production environment, you can change this to call to another API
	 */
	private WeatherInfo getWeatherInfo(String location, String unit) {
		WeatherInfo weather = new WeatherInfo();
		weather.setLocation(location);
		weather.setMeasurementUnit(unit);
		weather.setTemperature(23);
		return weather;
	}

}
