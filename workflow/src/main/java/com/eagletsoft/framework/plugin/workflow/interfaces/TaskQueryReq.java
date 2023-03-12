package com.eagletsoft.framework.plugin.workflow.interfaces;

import java.util.Date;

public class TaskQueryReq extends PageReq{
    private boolean includeCompleted;

    private boolean assigned;

    private String processDefinitionKey;

    private Date createdBefore;

    private Date createdAfter;

    public boolean isIncludeCompleted() {
        return includeCompleted;
    }

    public void setIncludeCompleted(boolean includeCompleted) {
        this.includeCompleted = includeCompleted;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Date getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(Date createdBefore) {
        this.createdBefore = createdBefore;
    }

    public Date getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Date createdAfter) {
        this.createdAfter = createdAfter;
    }
}
