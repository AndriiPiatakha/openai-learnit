package com.itbulls.learnit.openai.entities;

public class EmailData {
	private String addresseeEmail;
	private String addresseeName;
	private String subject;
	private String content;
	
	public String getAddresseeEmail() {
		return addresseeEmail;
	}
	public void setAddresseeEmail(String addresseeEmail) {
		this.addresseeEmail = addresseeEmail;
	}
	public String getAddresseeName() {
		return addresseeName;
	}
	public void setAddresseeName(String addresseeName) {
		this.addresseeName = addresseeName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
