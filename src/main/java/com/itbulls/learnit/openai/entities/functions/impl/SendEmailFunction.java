package com.itbulls.learnit.openai.entities.functions.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.itbulls.learnit.openai.entities.EmailData;
import com.itbulls.learnit.openai.entities.functions.Function;
import com.itbulls.learnit.openai.services.MailService;

public class SendEmailFunction implements Function {

	@Autowired
	private MailService mailService;
	@Autowired
	private Gson gson;

	@Override
	public String execute(String arguments) {
		EmailData emailData = gson.fromJson(arguments, EmailData.class);
		if (emailData.getAddresseeEmail() == null & emailData.getAddresseeName() == null) {
			return "Email was not sent. Please, specify full name or email of addressee";
		}
		if (emailData.getAddresseeEmail() == null & emailData.getAddresseeName() != null) {
			emailData.setAddresseeEmail(mailService.extractEmailByFullName(emailData.getAddresseeName()));
			if (emailData.getAddresseeEmail() == null) {
				return "Email was not sent. Please, specify email of addressee";
			}
		}
		
		if (mailService.sendEmail(emailData)) {
			return "Email was sent successfully";
		} else {
			return "Email was not sent. Some exception is happened, please, try one more time later";
		}
	}

}
