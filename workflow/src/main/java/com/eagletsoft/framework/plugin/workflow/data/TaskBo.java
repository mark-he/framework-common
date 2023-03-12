package com.eagletsoft.framework.plugin.workflow.data;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TaskBo {
    private String id;
    private String name;
    private List<String> candidates;
    private String assignee;
    private String executionId;
    private String formKey;
    private Map<String, Object> processVariables;
    private Map<String, Object> variables;
    private String tenantId;
    private String definitionKey;
    private String processInstanceId;

    private Date createTime;
    private Date claimTime;
    private Date dueDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<String> candidates) {
        this.candidates = candidates;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public Map<String, Object> getProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(Map<String, Object> processVariables) {
        this.processVariables = processVariables;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(Date claimTime) {
        this.claimTime = claimTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDefinitionKey() {
        return definitionKey;
    }

    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public static TaskBo from(DelegateTask task) {
        TaskBo taskBo = new TaskBo();
        taskBo.setName(task.getName());
        taskBo.setAssignee(task.getAssignee());
        taskBo.setId(task.getId());
        taskBo.setExecutionId(task.getExecutionId());
        taskBo.setFormKey(task.getFormKey());
        taskBo.setTenantId(task.getTenantId());
        taskBo.setCreateTime(task.getCreateTime());
        taskBo.setDueDate(task.getDueDate());
        taskBo.setDefinitionKey(task.getTaskDefinitionKey());
        taskBo.setProcessInstanceId(task.getProcessInstanceId());

        if (task instanceof TaskEntityImpl) {
            TaskEntityImpl impl = (TaskEntityImpl)task;
            taskBo.setProcessVariables(impl.getProcessVariables());
            taskBo.setVariables(impl.getTaskLocalVariables());
            taskBo.setClaimTime(impl.getClaimTime());

            List<String> candidates = new ArrayList<>();
            for (IdentityLink link : impl.getCandidates()) {
                candidates.add(link.getUserId());
            }

            taskBo.setCandidates(candidates);
        }
        return taskBo;
    }

    public static TaskBo from(Task task) {
        if (null == task) {
            return null;
        }
        TaskBo taskBo = new TaskBo();
        taskBo.setName(task.getName());
        taskBo.setAssignee(task.getAssignee());
        taskBo.setId(task.getId());
        taskBo.setExecutionId(task.getExecutionId());
        taskBo.setFormKey(task.getFormKey());
        taskBo.setProcessVariables(task.getProcessVariables());
        taskBo.setVariables(task.getTaskLocalVariables());
        taskBo.setTenantId(task.getTenantId());
        taskBo.setCreateTime(task.getCreateTime());
        taskBo.setClaimTime(task.getClaimTime());
        taskBo.setDueDate(task.getDueDate());
        taskBo.setDefinitionKey(task.getTaskDefinitionKey());
        taskBo.setProcessInstanceId(task.getProcessInstanceId());

        /*
        if (task instanceof TaskEntityImpl) {
            TaskEntityImpl impl = (TaskEntityImpl)task;

            List<String> candidates = new ArrayList<>();
            for (IdentityLink link : impl.getCandidates()) {
                candidates.add(link.getUserId());
            }

            taskBo.setCandidates(candidates);
        }
         */

        return taskBo;
    }

    public static TaskBo from(TaskEntityImpl task) {
        if (null == task) {
            return null;
        }
        TaskBo taskBo = new TaskBo();
        taskBo.setName(task.getName());
        taskBo.setAssignee(task.getAssignee());
        taskBo.setId(task.getId());
        taskBo.setExecutionId(task.getExecutionId());
        taskBo.setFormKey(task.getFormKey());
        taskBo.setProcessVariables(task.getProcessVariables());
        taskBo.setVariables(task.getTaskLocalVariables());
        taskBo.setTenantId(task.getTenantId());
        taskBo.setCreateTime(task.getCreateTime());
        taskBo.setClaimTime(task.getClaimTime());
        taskBo.setDueDate(task.getDueDate());
        taskBo.setDefinitionKey(task.getTaskDefinitionKey());
        taskBo.setProcessInstanceId(task.getProcessInstanceId());

        List<String> candidates = new ArrayList<>();
        for (IdentityLink link : task.getCandidates()) {
            candidates.add(link.getUserId());
        }
        taskBo.setCandidates(candidates);

        return taskBo;
    }


    public static List<TaskBo> from(List<Task> list) {
        List<TaskBo> ret = new ArrayList<>();
        for (Task task : list) {
            ret.add(TaskBo.from(task));
        }
        return ret;
    }

    public static List<TaskBo> from2(List<HistoricTaskInstanceEntityImpl> list) {
        List<TaskBo> ret = new ArrayList<>();
        for (HistoricTaskInstanceEntityImpl task : list) {
            ret.add(TaskBo.from2(task));
        }
        return ret;
    }

    public static TaskBo from2(HistoricTaskInstanceEntityImpl task) {
        if (null == task) {
            return null;
        }
        TaskBo taskBo = new TaskBo();
        taskBo.setName(task.getName());
        taskBo.setAssignee(task.getAssignee());
        taskBo.setId(task.getId());
        taskBo.setExecutionId(task.getExecutionId());
        taskBo.setFormKey(task.getFormKey());
        taskBo.setProcessVariables(task.getProcessVariables());
        taskBo.setVariables(task.getTaskLocalVariables());
        taskBo.setTenantId(task.getTenantId());
        taskBo.setCreateTime(task.getCreateTime());
        taskBo.setClaimTime(task.getClaimTime());
        taskBo.setDueDate(task.getDueDate());
        taskBo.setDefinitionKey(task.getTaskDefinitionKey());
        taskBo.setProcessInstanceId(task.getProcessInstanceId());

        return taskBo;
    }
}
