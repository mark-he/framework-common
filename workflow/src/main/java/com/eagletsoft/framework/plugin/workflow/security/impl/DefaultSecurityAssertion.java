package com.eagletsoft.framework.plugin.workflow.security.impl;

import com.eagletsoft.framework.plugin.workflow.UserProcessManager;
import com.eagletsoft.framework.plugin.workflow.WorkflowContext;
import com.eagletsoft.framework.plugin.workflow.errors.WorkflowException;
import com.eagletsoft.framework.plugin.workflow.security.SecurityAssertion;
import com.eagletsoft.framework.plugin.workflow.utils.ActivitiConstants;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.HistoricDetailEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class DefaultSecurityAssertion implements SecurityAssertion {

    @Override
    public void assertRead(String tenantId) {
        WorkflowContext ctx = WorkflowContext.get();
        if (ctx.getTenantId().equals(tenantId)) {
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.TENANT_NOT_READ, "Assert Tenant failed.");
        }
    }

    @Override
    public void assertRead(HistoricProcessInstance pi) {
        if (isAdminOfTenant(pi.getTenantId())) {
        } else if (UserProcessManager.getInstance().isRelatedToProcess(pi.getId())) {
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.PROCESS_NOT_READ, "Assert Process failed.");
        }
    }

    @Override
    public void assertManage(HistoricProcessInstance pi) {
        WorkflowContext ctx = WorkflowContext.get();
        if (isAdminOfTenant(pi.getTenantId())) {

        } else if (pi.getStartUserId().equals(ctx.getUserId())) {

        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.PROCESS_NOT_MANAGE, "Assert Manage failed.");
        }
    }

    @Override
    public void assertManage(String tenantId) {
        if (isAdminOfTenant(tenantId)) {
            return;
        }
        throw new WorkflowException(ActivitiConstants.ERROR_KEYS.NOT_TENANT_MANAGE, "Assert Manage failed.");
    }

    private boolean isAdminOfTenant(String tenantId) {
        WorkflowContext ctx = WorkflowContext.get();
        return (ctx.isSuperRight() && tenantId.equals(ctx.getTenantId()));
    }
}
