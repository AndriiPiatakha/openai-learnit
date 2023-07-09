package com.itbulls.learnit.openai.slack;

public interface SlackService {

	void processOnMentionEvent(String requestBody);

	void removeAllMessagesFromChannel(String channelId);

}
