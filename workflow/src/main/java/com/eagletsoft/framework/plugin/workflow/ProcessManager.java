package com.eagletsoft.framework.plugin.workflow;

import com.eagletsoft.framework.plugin.workflow.data.*;
import com.eagletsoft.framework.plugin.workflow.errors.WorkflowException;
import com.eagletsoft.framework.plugin.workflow.image.ImageGenerator;
import com.eagletsoft.framework.plugin.workflow.interfaces.*;
import com.eagletsoft.framework.plugin.workflow.security.SecurityAssertion;
import com.eagletsoft.framework.plugin.workflow.security.impl.DefaultSecurityAssertion;
import com.eagletsoft.framework.plugin.workflow.utils.ActivitiConstants;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessManager {
    private static Logger LOG = LoggerFactory.getLogger(ProcessManager.class);
    private static ProcessManager INSTANCE = new ProcessManager();
    private SecurityAssertion securityAssertion = new DefaultSecurityAssertion();

    private ProcessManager() {}

    public static ProcessManager getInstance() {
        return INSTANCE;
    }

    public SecurityAssertion getSecurityAssertion() {
        return securityAssertion;
    }

    public void setSecurityAssertion(SecurityAssertion securityAssertion) {
        this.securityAssertion = securityAssertion;
    }

    public InputStream generateImage(String tenantId, String id) throws Exception {
        HistoricProcessInstance pi = this.findNativeById(tenantId, id);
        this.securityAssertion.assertRead(pi);

        return ImageGenerator.generate(id);
    }

    protected HistoricProcessInstance findNativeById(String tenantId, String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstance hpi = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceId(id)
                .includeProcessVariables()
                .processInstanceTenantId(tenantId)
                .singleResult();

        return hpi;
    }

    public ProcessInstanceBo findById(String tenantId, String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstance pi = this.findNativeById(tenantId, id);

        this.securityAssertion.assertRead(pi);

        return ProcessInstanceBo.fromHistory(pi);
    }

    public List<ProcessInstanceBo> findByBusinessKey(String tenantId, String businessKey) {
        this.securityAssertion.assertManage(tenantId);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<HistoricProcessInstance> list = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .includeProcessVariables()
                .processInstanceTenantId(tenantId)
                .list();

        return ProcessInstanceBo.fromHistory(list);
    }

    private Task findNativeTaskById(String tenantId, String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Task task = processEngine.getTaskService().createTaskQuery()
                .taskId(id)
                .taskTenantId(tenantId)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .singleResult();
        return task;
    }

    public PageResult search(String tenantId, ProcessInstanceQueryReq req) {
        this.securityAssertion.assertManage(tenantId);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstanceQuery query = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery();

        query.processInstanceTenantId(tenantId);

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

    protected List<CommentBo> findComments(String tenantId, String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Task task = findNativeTaskById(tenantId, id);
        if (null != task) {
            List<Comment> list = processEngine.getTaskService().getTaskComments(id);
            return CommentBo.from(list);
        }
        return null;
    }

    public List<TaskDetailBo> findTaskDetailsByProcessInstance(String tenantId, String id) {
        HistoricProcessInstance pi = ProcessManager.getInstance().findNativeById(tenantId, id);
        this.securityAssertion.assertRead(pi);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstance hisProcessInstance = (HistoricProcessInstance) processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(id)
                .singleResult();
        List<HistoricTaskInstance> tasks = getHistoricTasks(hisProcessInstance
                .getId());
        List<TaskDetailBo> ret = new ArrayList<>();

        for (HistoricTaskInstance task: tasks) {
            ret.add(TaskDetailBo.from(task, this.findComments(tenantId, task.getId())));
        }
        return ret;
    }

    protected List<HistoricTaskInstance> getHistoricTasks(String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<HistoricTaskInstance> list = processEngine.getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(id)
                .includeTaskLocalVariables()
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list();
        return list;
    }
}
