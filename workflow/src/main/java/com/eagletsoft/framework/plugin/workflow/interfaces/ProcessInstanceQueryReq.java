package com.eagletsoft.framework.plugin.workflow.interfaces;

import java.util.Date;

public class ProcessInstanceQueryReq extends PageReq{
    private boolean includeFinished;

    private String processDefinitionKey;

    private Date startedBefore;

    private Date startedAfter;

    public boolean isIncludeFinished() {
        return includeFinished;
    }

    public void setIncludeFinished(boolean includeFinished) {
        this.includeFinished = includeFinished;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Date getStartedAfter() {
        return startedAfter;
    }

    public void setStartedAfter(Date startedAfter) {
        this.startedAfter = startedAfter;
    }

    public Date getStartedBefore() {
        return startedBefore;
    }

    public void setStartedBefore(Date startedBefore) {
        this.startedBefore = startedBefore;
    }
}
