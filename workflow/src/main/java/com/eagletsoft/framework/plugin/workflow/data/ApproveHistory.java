package com.eagletsoft.framework.plugin.workflow.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ApproveHistory implements Serializable {

    private String task;
    private String userId;
    private Map variables;
    private String comment;
    private boolean approved;
    private Date operationTime;

    public ApproveHistory(String task, String userId, boolean approved, String comment, Date operationTime) {
        this.task = task;
        this.userId = userId;
        this.approved = approved;
        this.comment = comment;
        this.operationTime = operationTime;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map getVariables() {
        return variables;
    }

    public void setVariables(Map variables) {
        this.variables = variables;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }
}
