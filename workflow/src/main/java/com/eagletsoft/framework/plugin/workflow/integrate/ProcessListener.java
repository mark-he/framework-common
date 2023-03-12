package com.eagletsoft.framework.plugin.workflow.integrate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;

public interface ProcessListener {
    void onProcess(String event, DelegateExecution execution);

    void onTask(String event, DelegateTask task);
}
