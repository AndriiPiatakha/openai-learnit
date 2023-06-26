package com.itbulls.learnit.openai.entities.functions;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.itbulls.learnit.openai.BeansConfiguration;

@Service
public class FunctionFactory {
	
	private AnnotationConfigApplicationContext context;

    public FunctionFactory() {
        context = new AnnotationConfigApplicationContext(BeansConfiguration.class);
    }
	
	public Function getFunctionByName(String functionName) {
		return (Function) context.getBean(functionName);
	}

}
