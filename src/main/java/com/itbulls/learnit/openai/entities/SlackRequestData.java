package com.itbulls.learnit.openai.entities;

public class SlackRequestData {
	
	private String authorName;
	private String message;
	private String channelIdFrom;
	
	public SlackRequestData(String authorName, String message, String channelIdFrom) {
		this.authorName = authorName;
		this.message = message;
		this.channelIdFrom = channelIdFrom;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getChannelIdFrom() {
		return channelIdFrom;
	}
	public void setChannelIdFrom(String channelIdFrom) {
		this.channelIdFrom = channelIdFrom;
	}
}
