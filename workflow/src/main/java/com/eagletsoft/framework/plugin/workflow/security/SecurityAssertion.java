package com.eagletsoft.framework.plugin.workflow.security;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public interface SecurityAssertion {
    void assertManage(HistoricProcessInstance pi);
    void assertManage(String tenantId);

    void assertRead(String tenantId);
    void assertRead(HistoricProcessInstance pi);
}
