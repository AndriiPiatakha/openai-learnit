package com.itbulls.learnit.openai.entities.functions;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.itbulls.learnit.openai.BeansConfiguration;

@Service
public class FunctionFactory implements ApplicationContextAware {
	
	private ApplicationContext applicationContext;

	public Function getFunctionByName(String functionName) {
		return (Function) applicationContext.getBean(functionName);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
