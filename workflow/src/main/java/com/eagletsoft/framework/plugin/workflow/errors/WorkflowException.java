package com.eagletsoft.framework.plugin.workflow.errors;

public class WorkflowException extends RuntimeException {
    private String key;

    public WorkflowException(String key, String message) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
