package com.eagletsoft.framework.plugin.workflow.custom;

import com.eagletsoft.framework.plugin.workflow.integrate.ProcessListener;
import org.activiti.engine.delegate.BaseExecutionListener;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class GeneralExecutionListener implements ExecutionListener, BaseExecutionListener {

    @Override
    public void notify(DelegateExecution execution) {
        ProcessListener processListener = TenantListenerManager.getInstance().get(execution.getTenantId());
        if (null != processListener) {
            processListener.onProcess(execution.getEventName(), execution);
        }
    }
}
