package com.itbulls.learnit.openai.entities;

public class CreateJiraIssueRequest {
	
	private Fields fields;
	
	public class Fields {
		private Assignee assignee;
		private String description;
		private String duedate;
		private IssueType issuetype;
		private Project project;
		private String summary;
		
		public class Assignee {
			private String id;
			public Assignee() {}
			public Assignee(String id) {
				this.id = id;
			}
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
		}
		
		public class IssueType {
			private String name;
			public IssueType() {}
			public IssueType(String name) {
				this.name = name;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
		}
		
		public class Project {
			private String key;
			public Project() {}
			public Project(String key) {
				this.key = key;
			}
			public String getKey() {
				return key;
			}
			public void setKey(String key) {
				this.key = key;
			}
		}

		public Assignee getAssigneee() {
			return assignee;
		}

		public void setAssigneee(Assignee assignee) {
			this.assignee = assignee;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getDuedate() {
			return duedate;
		}

		public void setDuedate(String duedate) {
			this.duedate = duedate;
		}

		public IssueType getIssuetype() {
			return issuetype;
		}

		public void setIssuetype(IssueType issuetype) {
			this.issuetype = issuetype;
		}

		public Project getProject() {
			return project;
		}

		public void setProject(Project project) {
			this.project = project;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}
	}

	public Fields getFields() {
		return fields;
	}

	public void setFields(Fields fields) {
		this.fields = fields;
	}
}
