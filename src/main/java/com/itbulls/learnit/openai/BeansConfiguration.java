package com.itbulls.learnit.openai;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
public class BeansConfiguration {
	
	@Bean
	public Gson gson() {
		/* Need to set field naming policy in order to convert attribute names
		 * from and to Java-style. For example:
		 * presencePenalty ==> presence_penalty 
		 * completion_tokens ==> completionTokens
		 * finish_reason ==> finishReason
		 */
		
		return new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}
	
	@Bean
	public HttpClient httpClient() {
		return HttpClientBuilder.create().build();
	}

}
