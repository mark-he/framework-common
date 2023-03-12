package com.eagletsoft.framework.plugin.workflow.interfaces;

public class ProcessDefinitionQueryReq extends PageReq{
    private boolean includeSuspended;

    public boolean isIncludeSuspended() {
        return includeSuspended;
    }

    public void setIncludeSuspended(boolean includeSuspended) {
        this.includeSuspended = includeSuspended;
    }
}
