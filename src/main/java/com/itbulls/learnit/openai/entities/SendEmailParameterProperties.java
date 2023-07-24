package com.itbulls.learnit.openai.entities;

public class SendEmailParameterProperties implements ParameterProperties {
	private Subject subject;
	private Content content;
	private AddresseeEmail addresseeEmail;
	private AddresseeName addresseeName;
	
	public class Subject extends ParameterPropertyAttribute {
		public Subject() {}
		public Subject(String type, String description) {
			super(type, description);
		}
	}
	
	public class Content extends ParameterPropertyAttribute {
		public Content() {}
		public Content(String type, String description) {
			super(type, description);
		}
	}
	
	public class AddresseeEmail extends ParameterPropertyAttribute {
		public AddresseeEmail() {}
		public AddresseeEmail(String type, String description) {
			super(type, description);
		}
	}
	
	public class AddresseeName extends ParameterPropertyAttribute {
		public AddresseeName() {}
		public AddresseeName(String type, String description) {
			super(type, description);
		}
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public AddresseeEmail getAddresseeEmail() {
		return addresseeEmail;
	}

	public void setAddresseeEmail(AddresseeEmail addressee) {
		this.addresseeEmail = addressee;
	}

	public AddresseeName getAddresseeName() {
		return addresseeName;
	}

	public void setAddresseeName(AddresseeName addresseeName) {
		this.addresseeName = addresseeName;
	}
}
