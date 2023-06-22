package com.itbulls.learnit.openai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@Configuration
@PropertySources({
	@PropertySource("classpath:myapp.properties"),
	@PropertySource("classpath:secrets.properties")
})
public class OpenaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenaiApplication.class, args);
	}

}
