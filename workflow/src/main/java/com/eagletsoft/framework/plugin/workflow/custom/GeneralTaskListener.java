package com.eagletsoft.framework.plugin.workflow.custom;

import com.eagletsoft.framework.plugin.workflow.integrate.ProcessListener;
import org.activiti.engine.delegate.BaseTaskListener;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class GeneralTaskListener implements TaskListener, BaseTaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

        ProcessListener processListener = TenantListenerManager.getInstance().get(delegateTask.getTenantId());
        if (null != processListener) {
            processListener.onTask(delegateTask.getEventName(), delegateTask);
        }
    }
}
