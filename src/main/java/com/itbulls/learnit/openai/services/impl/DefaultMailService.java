package com.itbulls.learnit.openai.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itbulls.learnit.openai.entities.EmailData;
import com.itbulls.learnit.openai.services.MailService;

@Service
public class DefaultMailService implements MailService {

	@Value("${mail.sender.email}")
	private String senderEmail;
	@Value("${mail.sender.email.password}")
	private String senderPassword;
	@Value("${mail.receiver.test.email}")
	private String receiverTestEmail;
	
	@Override
	public boolean sendEmail(String addresseeEmail, String subject, String content) {
		// SMTP server configuration
		String smtpHost = "smtp.gmail.com";
		int smtpPort = 465;
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.port", smtpPort);
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.port", smtpPort);

			// Create a Session object with authentication
			Session session = Session.getInstance(props, new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(senderEmail, senderPassword);
	            }
	        });

			// Create the email message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addresseeEmail));
			message.setSubject(subject);

			// Create multipart content for the email
			Multipart multipart = new MimeMultipart();

			// Create the text part of the email
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(content);
			multipart.addBodyPart(textPart);

			// Set the multipart content as the email's content
			message.setContent(multipart);

			// Send the email
			Transport.send(message);

			System.out.println("Email was sent successfully.");
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean sendEmail(EmailData emailData) {
		return sendEmail(emailData.getAddresseeEmail(), emailData.getSubject(), emailData.getContent());
	}

	/* TODO: implement integration with email addresses data source. 
	 * It can be either Active Directory, or GSuite service, or excel spreadsheet, or anything else. 
	 * The main thing is to configure any emails data source you wish. 
	 * For the sake of this implementation I hardcoded map with email addresses. 
	 */
	@Override
	public String extractEmailByFullName(String addresseeName) {
		Map<String, String> fullNameToEmailMap = new HashMap<>();
		fullNameToEmailMap.put("Andrey Pyatakha", "andrey.pyatakha@example.com");
		fullNameToEmailMap.put("John Doe", receiverTestEmail);
		return fullNameToEmailMap.get(addresseeName);
	}

}
