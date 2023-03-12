package com.eagletsoft.framework.plugin.workflow;

import com.eagletsoft.boot.framework.common.utils.JsonUtils;
import com.eagletsoft.framework.plugin.workflow.data.*;
import com.eagletsoft.framework.plugin.workflow.errors.WorkflowException;
import com.eagletsoft.framework.plugin.workflow.interfaces.*;
import com.eagletsoft.framework.plugin.workflow.security.SecurityAssertion;
import com.eagletsoft.framework.plugin.workflow.security.impl.DefaultSecurityAssertion;
import com.eagletsoft.framework.plugin.workflow.utils.ActivitiConstants;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProcessManager {
    private static Logger LOG = LoggerFactory.getLogger(ProcessManager.class);
    private static UserProcessManager INSTANCE = new UserProcessManager();
    private SecurityAssertion securityAssertion = new DefaultSecurityAssertion();

    public static UserProcessManager getInstance() {
        return INSTANCE;
    }

    private ProcessDefinition findNativeStartableProcessByKey(String tenantId, String key) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessDefinition def = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .processDefinitionTenantId(tenantId)
                .latestVersion()
                //.startableByUser(WorkflowContext.get().getUserId())
                .active()
                .singleResult();

        return def;
    }

    private Task findNaiveCandidateTask(String tenantId, String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        WorkflowContext ctx = WorkflowContext.get();

        Task task = processEngine.getTaskService().createTaskQuery()
                .taskTenantId(tenantId)
                .taskId(id)
                .taskCandidateUser(ctx.getUserId())
                .singleResult()
                ;
        return task;
    }

    private Task findNaiveTask(String tenantId, String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        WorkflowContext ctx = WorkflowContext.get();

        Task task = processEngine.getTaskService().createTaskQuery()
                .taskTenantId(tenantId)
                .taskId(id)
                .taskAssignee(ctx.getUserId())
                .singleResult()
                ;
        return task;
    }

    protected Map initProcessVariables(String tenantId, StartProcessReq req) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("submit", req.getVariables());

        ProcessState state = new ProcessState();
        state.getHistory().add(new ApproveHistory("", WorkflowContext.get().getUserId(), true, req.getComment(), new Date()));
        variables.put("state", state);

        return variables;
    }

    public ProcessInstanceBo start(String tenantId, StartProcessReq req) {
        this.securityAssertion.assertRead(tenantId);
        ProcessDefinition def = this.findNativeStartableProcessByKey(tenantId, req.getKey());
        if (null != def) {
            Map<String, Object> variables = this.initProcessVariables(tenantId, req);

            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            ProcessInstance pi = processEngine.getRuntimeService()
                    .startProcessInstanceByKeyAndTenantId(req.getKey(), req.getBusinessKey(), variables, tenantId);

            ProcessInstanceBo ret = ProcessInstanceBo.from(pi);
            ret.setVariables(variables);
            return ret;
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.PROCESS_NOT_FOUND, "Not Found");
        }
    }

    public InputStream generateImage(String tenantId, String id) throws Exception {
        HistoricProcessInstance pi = ProcessManager.getInstance().findNativeById(tenantId, id);
        this.securityAssertion.assertRead(pi);

        if (null != pi) {
            return ProcessManager.getInstance().generateImage(tenantId, id);
        }
        return null;
    }

    public void terminate(String tenantId, String id, TerminateProcessReq req) {
        HistoricProcessInstance pi = ProcessManager.getInstance().findNativeById(tenantId, id);
        this.securityAssertion.assertManage(pi);

        if (null != pi) {
            try {
                ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
                processEngine.getRuntimeService().deleteProcessInstance(id, req.getReason());
            } catch (Exception e) {
                e.printStackTrace();
                LOG.info("删除流程实例报错...");
            }
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.PROCESS_NOT_MANAGE, "Not Manage");
        }
    }

    public void active(String tenantId, String id) {
        HistoricProcessInstance pi = ProcessManager.getInstance().findNativeById(tenantId, id);
        this.securityAssertion.assertManage(pi);

        if (null != pi) {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            processEngine.getRuntimeService().activateProcessInstanceById(id);
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.PROCESS_NOT_MANAGE, "Not Manage");
        }
    }

    public void suspend(String tenantId, String id) {
        HistoricProcessInstance pi = ProcessManager.getInstance().findNativeById(tenantId, id);
        this.securityAssertion.assertManage(pi);

        if (null != pi) {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            processEngine.getRuntimeService().suspendProcessInstanceById(id);
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.PROCESS_NOT_MANAGE, "Not Manage");
        }
    }

    public PageResult searchMyStarted(String tenantId, ProcessInstanceQueryReq req) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstanceQuery query = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery();

        query.processInstanceTenantId(tenantId);

        if (null != req.getProcessDefinitionKey()) {
            query.processDefinitionKey(req.getProcessDefinitionKey());
        }
        if (null != req.getStartedAfter()) {
            query.startedAfter(req.getStartedAfter());
        }
        if (null != req.getStartedBefore()) {
            query.startedBefore(req.getStartedBefore());
        }

        query.startedBy(WorkflowContext.get().getUserId());

        if (!req.isIncludeFinished()) {
            query.unfinished();
        }

        query.includeProcessVariables()
                .orderByProcessInstanceStartTime()
                .desc();

        PageResult pr = PageResult.make(query, req);
        pr.setData(ProcessInstanceBo.fromHistory(pr.getData()));
        return pr;
    }

    public PageResult searchMyInvolved(String tenantId, ProcessInstanceQueryReq req) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstanceQuery query = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery();

        query.processInstanceTenantId(tenantId);
        if (null != req.getProcessDefinitionKey()) {
            query.processDefinitionKey(req.getProcessDefinitionKey());
        }
        if (null != req.getStartedAfter()) {
            query.startedAfter(req.getStartedAfter());
        }
        if (null != req.getStartedBefore()) {
            query.startedBefore(req.getStartedBefore());
        }

        query.involvedUser(WorkflowContext.get().getUserId());
        if (!req.isIncludeFinished()) {
            query.unfinished();
        } else {
            query.finished();
        }

        query.includeProcessVariables()
                .orderByProcessInstanceStartTime()
                .desc();

        PageResult pr = PageResult.make(query, req);
        pr.setData(ProcessInstanceBo.fromHistory(pr.getData()));
        return pr;
    }

    public List<TaskBo> findTasks(String tenantId, TaskQueryReq req, boolean assigned) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.taskTenantId(tenantId);

        WorkflowContext ctx = WorkflowContext.get();
        if (assigned) {
            query.taskAssignee(ctx.getUserId());
        } else {
//            query.taskCandidateUser()
            query.taskCandidateOrAssigned(ctx.getUserId());
        }

        if (null != req.getProcessDefinitionKey()) {
            query.processDefinitionKey(req.getProcessDefinitionKey());
        }
        if (null != req.getCreatedAfter()) {
            query.taskCreatedAfter(req.getCreatedAfter());
        }
        if (null != req.getCreatedBefore()) {
            query.taskCreatedBefore(req.getCreatedBefore());
        }

        if (!req.isIncludeCompleted()) {
            query.active();
        }

        query.includeProcessVariables()
                .includeTaskLocalVariables()
                .orderByTaskCreateTime()
                .desc();

        List<Task> list = query.list();
        return TaskBo.from(list);
    }

    public PageResult searchTasksIncludeVariables(String tenantId, TaskQueryReq req) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.taskTenantId(tenantId);

        WorkflowContext ctx = WorkflowContext.get();
        if (req.isAssigned()) {
            query.taskAssignee(ctx.getUserId());
        } else {
            query.taskCandidateOrAssigned(ctx.getUserId());
        }

        if (null != req.getProcessDefinitionKey()) {
            query.processDefinitionKey(req.getProcessDefinitionKey());
        }
        if (null != req.getCreatedAfter()) {
            query.taskCreatedAfter(req.getCreatedAfter());
        }
        if (null != req.getCreatedBefore()) {
            query.taskCreatedBefore(req.getCreatedBefore());
        }

        if (!req.isIncludeCompleted()) {
            query.active();
        }

        query.includeProcessVariables()
                .includeTaskLocalVariables()
                .orderByTaskCreateTime()
                .desc();

        PageResult pr = PageResult.make(query, req);
        pr.setData(TaskBo.from(pr.getData()));
        return pr;
    }

    public PageResult searchTasks(String tenantId, TaskQueryReq req) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.taskTenantId(tenantId);

        WorkflowContext ctx = WorkflowContext.get();
        if (req.isAssigned()) {
            query.taskAssignee(ctx.getUserId());
        } else {
            query.taskCandidateOrAssigned(ctx.getUserId());
        }

        if (null != req.getProcessDefinitionKey()) {
            query.processDefinitionKey(req.getProcessDefinitionKey());
        }
        if (null != req.getCreatedAfter()) {
            query.taskCreatedAfter(req.getCreatedAfter());
        }
        if (null != req.getCreatedBefore()) {
            query.taskCreatedBefore(req.getCreatedBefore());
        }

        if (!req.isIncludeCompleted()) {
            query.active();
        }

        query.orderByTaskCreateTime()
                .desc();

        PageResult pr = PageResult.make(query, req);
        pr.setData(TaskBo.from(pr.getData()));
        return pr;
    }

    public PageResult searchHistoricTasks(String tenantId, TaskQueryReq req) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricTaskInstanceQuery query = processEngine.getHistoryService().createHistoricTaskInstanceQuery();
        query.taskTenantId(tenantId);

        WorkflowContext ctx = WorkflowContext.get();
        query.taskAssignee(ctx.getUserId());

        if (null != req.getProcessDefinitionKey()) {
            query.processDefinitionKey(req.getProcessDefinitionKey());
        }
        if (null != req.getCreatedAfter()) {
            query.taskCreatedAfter(req.getCreatedAfter());
        }
        if (null != req.getCreatedBefore()) {
            query.taskCreatedBefore(req.getCreatedBefore());
        }

        query.orderByTaskCreateTime()
                .desc();

        PageResult pr = PageResult.make(query, req);
        pr.setData(TaskBo.from2(pr.getData()));
        return pr;

    }


    protected Map getTaskUpdatedVariables(String tenantId, Task task, CompleteTaskReq req) {
        HistoricProcessInstance pi = ProcessManager.getInstance().findNativeById(tenantId, task.getProcessInstanceId());

        ProcessState state = (ProcessState)pi.getProcessVariables().getOrDefault("state", new ProcessState());

        boolean approved = false;

        Object obj = req.getVariables().get("approved");
        if (null != obj && obj instanceof Boolean) {
            approved = (Boolean)obj;
        }
        state.setApproved(approved);
        ApproveHistory history = new ApproveHistory(task.getName(), WorkflowContext.get().getUserId(), approved, req.getComment(), new Date());
        state.getHistory().add(history);

        Map variables = new HashMap();
        variables.putAll(req.getVariables());
        variables.put("state", state);

        return variables;
    }

    public void completeTask(String tenantId, String id, CompleteTaskReq req) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        Task task = findNaiveTask(tenantId, id);
        if (null != task) {
            Map variables = this.getTaskUpdatedVariables(tenantId, task, req);
            if (!StringUtils.isEmpty(req.getComment())) {
                processEngine.getTaskService().addComment(id, task.getProcessInstanceId(), req.getComment());
            }
            processEngine.getTaskService().setVariablesLocal(task.getId(), req.getVariables());
            if (null != task.getDelegationState() && task.getDelegationState().equals(DelegationState.PENDING)) {
                processEngine.getTaskService().resolveTask(id, variables);
            } else {
                processEngine.getTaskService().complete(id, variables);
            }

            HistoricProcessInstance pi = ProcessManager.getInstance().findNativeById(tenantId, task.getProcessInstanceId());
            JsonUtils.writeValue(pi.getProcessVariables());
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.NOT_TASK_OWN_OR_MISSED, "Not Present");
        }
    }

    public void unclaimTask(String tenantId, String id) {
        Task task = findNaiveTask(tenantId, id);
        if (null != task) {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            processEngine.getTaskService().unclaim(task.getId());
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.NOT_TASK_OWN_OR_MISSED, "Not Present");
        }
    }

    public void claimTask(String tenantId, String id) {
        Task task = findNaiveCandidateTask(tenantId, id);
        if (null != task) {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            processEngine.getTaskService().claim(task.getId(), WorkflowContext.get().getUserId());
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.NOT_TASK_OWN_OR_MISSED, "Not Present");
        }
    }

    public void delegateTask(String tenantId, String id, String delegateUserId) {
        Task task = findNaiveTask(tenantId, id);
        if (null != task) {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            processEngine.getTaskService().delegateTask(task.getId(), delegateUserId);
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.NOT_TASK_OWN_OR_MISSED, "Not Present");
        }
    }

    public void addComment(String tenantId, String id, AddTaskCommentReq req) {
        Task task = findNaiveTask(tenantId, id);
        if (null != task) {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            processEngine.getTaskService().addComment(id, null, req.getComment());
        } else {
            throw new WorkflowException(ActivitiConstants.ERROR_KEYS.NOT_TASK_OWN_OR_MISSED, "Not Present");
        }
    }

    public TaskDetailBo findTaskDetail(String tenantId, String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Task task = processEngine.getTaskService().createTaskQuery()
                .taskId(id)
                .taskAssignee(WorkflowContext.get().getUserId())
                .taskTenantId(tenantId)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .singleResult();

        return TaskDetailBo.from(task, ProcessManager.getInstance().findComments(tenantId, id));
    }

    public boolean isRelatedToProcess(String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstance pi = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceId(id)
                .involvedUser(WorkflowContext.get().getUserId())
                .singleResult();

        return null != pi;
    }

}
