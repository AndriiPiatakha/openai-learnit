package com.itbulls.learnit.openai.services;

import com.itbulls.learnit.openai.entities.EmailData;

public interface MailService {
	
	boolean sendEmail(String addresseeEmail, String subject, String content);
	boolean sendEmail(EmailData emailData);
	String extractEmailByFullName(String addresseeName);

}
