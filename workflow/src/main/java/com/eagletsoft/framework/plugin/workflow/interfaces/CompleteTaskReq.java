package com.eagletsoft.framework.plugin.workflow.interfaces;

import java.util.Map;

public class CompleteTaskReq {
    private Map<String, Object> variables;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
