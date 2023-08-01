package com.itbulls.learnit.openai.entities;

public class JiraRisk extends JiraIssue {

	// customfield_10004
	private String impact;
	// customfield_10038
	private String probability;
	// customfield_10035
	private String riskResponseStrategy;
	// customfield_10036
	private String mitigationPlan;
	public String getImpact() {
		return impact;
	}
	public void setImpact(String impact) {
		this.impact = impact;
	}
	public String getProbability() {
		return probability;
	}
	public void setProbability(String probability) {
		this.probability = probability;
	}
	public String getRiskResponseStrategy() {
		return riskResponseStrategy;
	}
	public void setRiskResponseStrategy(String riskResponseStrategy) {
		this.riskResponseStrategy = riskResponseStrategy;
	}
	public String getMitigationPlan() {
		return mitigationPlan;
	}
	public void setMitigationPlan(String mitigationPlan) {
		this.mitigationPlan = mitigationPlan;
	}
}
