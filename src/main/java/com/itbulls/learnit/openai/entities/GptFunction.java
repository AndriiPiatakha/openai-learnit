package com.itbulls.learnit.openai.entities;

public class GptFunction {
	
	private String name;
	private String description;
	private Parameters parameters;
	
	
	public class Parameters {
		private String type;
		private ParameterProperties properties;
		
		// requiredProperties
		private String[] required;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public ParameterProperties getProperties() {
			return properties;
		}

		public void setProperties(ParameterProperties properties) {
			this.properties = properties;
		}

		public String[] getRequired() {
			return required;
		}

		public void setRequired(String[] required) {
			this.required = required;
		}
		
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Parameters getParameters() {
		return parameters;
	}
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	@Override
	public String toString() {
		return "GptFunction [name=" + name + ", description=" + description + ", parameters=" + parameters + "]";
	}
}
