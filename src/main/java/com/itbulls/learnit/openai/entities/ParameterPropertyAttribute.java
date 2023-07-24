package com.itbulls.learnit.openai.entities;

public abstract class ParameterPropertyAttribute {
	
	private String type;
	private String description;
	public ParameterPropertyAttribute() {
	}
	public ParameterPropertyAttribute(String type, String description) {
		this.type = type;
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
