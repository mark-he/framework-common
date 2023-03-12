package com.eagletsoft.framework.plugin.workflow.data;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessState implements Serializable {

//    private static final long serialVersionUID = 1L;
    private List<ApproveHistory> history = new ArrayList<>();
    private boolean approved;
    private String version = "1.0";

    public List<ApproveHistory> getHistory() {
        return history;
    }

    public void setHistory(List<ApproveHistory> history) {
        this.history = history;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ProcessState{" +
                "history=" + history +
                ", approved=" + approved +
                ", version='" + version + '\'' +
                '}';
    }
}
