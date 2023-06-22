package com.itbulls.learnit.openai.entities;

import java.util.List;

public class GptResponse {
	private String id;
	private String object;
	private Long created;
	private String model;
	private List<Choice> choices;
	private Usage usage;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public List<Choice> getChoices() {
		return choices;
	}
	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}
	public Usage getUsage() {
		return usage;
	}
	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public class Choice {
		private Integer index;
		private GptMessage message;
		private String finishReason;
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		public GptMessage getMessage() {
			return message;
		}
		public void setMessage(GptMessage message) {
			this.message = message;
		}
		public String getFinishReason() {
			return finishReason;
		}
		public void setFinishReason(String finishReason) {
			this.finishReason = finishReason;
		}
		@Override
		public String toString() {
			return "Choice [index=" + index + ", message=" + message + ", finishReason=" + finishReason + "]";
		}
	}
	
	public class Usage {
		private Integer promptTokens;
		private Integer completionTokens;
		private Integer totalTokens;
		public Integer getPromptTokens() {
			return promptTokens;
		}
		public void setPromptTokens(Integer promptTokens) {
			this.promptTokens = promptTokens;
		}
		public Integer getCompletionTokens() {
			return completionTokens;
		}
		public void setCompletionTokens(Integer completionTokens) {
			this.completionTokens = completionTokens;
		}
		public Integer getTotalTokens() {
			return totalTokens;
		}
		public void setTotalTokens(Integer totalTokens) {
			this.totalTokens = totalTokens;
		}
		@Override
		public String toString() {
			return "Usage [promptTokens=" + promptTokens + ", completionTokens=" + completionTokens + ", totalTokens="
					+ totalTokens + "]";
		}
		
	}

	@Override
	public String toString() {
		return "GptResponse [id=" + id + ", object=" + object + ", created=" + created + ", model=" + model
				+ ", choices=" + choices + ", usage=" + usage + "]";
	}
	
	

}
