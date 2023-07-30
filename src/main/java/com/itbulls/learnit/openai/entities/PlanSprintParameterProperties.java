package com.itbulls.learnit.openai.entities;

public class PlanSprintParameterProperties implements ParameterProperties {
	private SprintName sprintName;
	private Capacity capacity;
	
	public class SprintName extends ParameterPropertyAttribute {
		public SprintName() {}
		public SprintName(String type, String description) {
			super(type, description);
		}
	}
	
	public class Capacity extends ParameterPropertyAttribute {
		public Capacity() {}
		public Capacity(String type, String description) {
			super(type, description);
		}
	}

	public SprintName getSprintName() {
		return sprintName;
	}

	public void setSprintName(SprintName sprintName) {
		this.sprintName = sprintName;
	}

	public Capacity getCapacity() {
		return capacity;
	}

	public void setCapacity(Capacity capacity) {
		this.capacity = capacity;
	}
	

}
