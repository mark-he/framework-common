package com.eagletsoft.framework.plugin.workflow;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;

public class WorkflowContext {
    private static ThreadLocal<WorkflowContext> KEEP = new ThreadLocal<>();
    private String tenantId;
    private String userId;
    private boolean superRight;

    public static WorkflowContext init(String tenantId, String userId) {
        WorkflowContext ctx = new WorkflowContext();
        ctx.setTenantId(tenantId);
        ctx.setUserId(userId);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getIdentityService().setAuthenticatedUserId(userId);

        KEEP.set(ctx);
        return ctx;
    }

    public static WorkflowContext get() {
        return KEEP.get();
    }

    public static void destroy() {
        KEEP.remove();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSuperRight() {
        return superRight;
    }

    public void setSuperRight(boolean superRight) {
        this.superRight = superRight;
    }
}
