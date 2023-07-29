package com.itbulls.learnit.openai.entities;

public class JiraSprint {
	public static final String CLOSED_STATE = "closed";
	
    private Integer id;
    private String self;
    private String state;
    private String name;
    private String startDate;
    private String endDate;
    private String completeDate;
    private Integer originBoardId;
    private String goal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public Integer getOriginBoardId() {
        return originBoardId;
    }

    public void setOriginBoardId(Integer originBoardId) {
        this.originBoardId = originBoardId;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
