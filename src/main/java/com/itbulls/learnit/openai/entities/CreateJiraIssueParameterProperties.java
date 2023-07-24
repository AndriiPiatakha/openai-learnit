package com.itbulls.learnit.openai.entities;

public class CreateJiraIssueParameterProperties implements ParameterProperties {
	
	private IssueType issueType;
	private Assignee assignee;
	private Summary summary;
	private Description description;
	private DueDate dueDate;

	public class IssueType extends ParameterPropertyAttribute {
		private String[] enumValues;
		
		public IssueType() {}
		public IssueType(String type, String description, String[] enumValues) {
			super(type, description);
			this.enumValues = enumValues;
		}
		public String[] getEnumValues() {
			return enumValues;
		}
		public void setEnumValues(String[] enumValues) {
			this.enumValues = enumValues;
		}
	}
	
	public class Assignee extends ParameterPropertyAttribute {
		public Assignee() {}
		public Assignee(String type, String description) {
			super(type, description);
		}
	}
	
	public class Summary extends ParameterPropertyAttribute {
		public Summary() {}
		public Summary(String type, String description) {
			super(type, description);
		}
	}
	
	public class Description extends ParameterPropertyAttribute {
		public Description() {}
		public Description(String type, String description) {
			super(type, description);
		}
	}
	
	public class DueDate extends ParameterPropertyAttribute {
		private String dueDateFormat;
		
		public DueDate() {}
		public DueDate(String type, String description, String dueDateFormat) {
			super(type, description);
			this.dueDateFormat = dueDateFormat;
		}
		public String getDueDateFormat() {
			return dueDateFormat;
		}
		public void setDueDateFormat(String dueDateFormat) {
			this.dueDateFormat = dueDateFormat;
		}
		
	}

	public IssueType getIssueType() {
		return issueType;
	}

	public void setIssueType(IssueType issueType) {
		this.issueType = issueType;
	}

	public Assignee getAssignee() {
		return assignee;
	}

	public void setAssignee(Assignee assignee) {
		this.assignee = assignee;
	}

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public DueDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(DueDate dueDate) {
		this.dueDate = dueDate;
	}
}
