package com.itbulls.learnit.openai.entities;

public class GetAvgVelocityParameterProperties implements ParameterProperties {

	private AmountOfCompletedSprints amountOfCompletedSprints;
	
	public class AmountOfCompletedSprints extends ParameterPropertyAttribute {
		public AmountOfCompletedSprints() {}
		public AmountOfCompletedSprints(String type, String description) {
			super(type, description);
		}
	}

	public AmountOfCompletedSprints getAmountOfCompletedSprints() {
		return amountOfCompletedSprints;
	}

	public void setAmountOfCompletedSprints(AmountOfCompletedSprints amountOfCompletedSprints) {
		this.amountOfCompletedSprints = amountOfCompletedSprints;
	}
	
}
