package com.eagletsoft.framework.plugin.workflow.data;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProcessInstanceBo {

    private String id;
    private String definitionId;
    private String businessKey;
    private String name;
    private String startUserId;
    private Date startTime;
    private Date endTime;
    private Long duration;
    private Map<String, Object> variables;
    private String tenantId;
    private String definitionName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public static ProcessInstanceBo from(ProcessInstance pi) {
        if (null == pi) {
            return null;
        }
        ProcessInstanceBo processInstanceBo = new ProcessInstanceBo();

        processInstanceBo.setId(pi.getId());
        processInstanceBo.setName(pi.getName());
        processInstanceBo.setBusinessKey(pi.getBusinessKey());
        processInstanceBo.setDefinitionId(pi.getProcessDefinitionId());
        processInstanceBo.setStartUserId(pi.getStartUserId());
        processInstanceBo.setStartTime(pi.getStartTime());
        processInstanceBo.setVariables(pi.getProcessVariables());
        processInstanceBo.setTenantId(pi.getTenantId());
        processInstanceBo.setDefinitionName(pi.getProcessDefinitionName());
/*
        if (pi instanceof ExecutionEntityImpl) {
            processInstanceBo.setVariables(((ExecutionEntityImpl) pi).getVariables());
        }
*/
        return processInstanceBo;
    }

    public static List<ProcessInstanceBo> from(List<ProcessInstance> list) {
        List<ProcessInstanceBo> ret = new ArrayList<>();
        for (ProcessInstance pi : list) {
            ret.add(ProcessInstanceBo.from(pi));
        }
        return ret;
    }

    public static ProcessInstanceBo fromHistory(HistoricProcessInstance pi) {
        ProcessInstanceBo processInstanceBo = new ProcessInstanceBo();

        processInstanceBo.setId(pi.getId());
        processInstanceBo.setName(pi.getName());
        processInstanceBo.setBusinessKey(pi.getBusinessKey());
        processInstanceBo.setDefinitionId(pi.getProcessDefinitionId());
        processInstanceBo.setStartUserId(pi.getStartUserId());
        processInstanceBo.setStartTime(pi.getStartTime());
        processInstanceBo.setVariables(pi.getProcessVariables());
        processInstanceBo.setEndTime(pi.getEndTime());
        processInstanceBo.setDuration(pi.getDurationInMillis());
        processInstanceBo.setTenantId(pi.getTenantId());
        processInstanceBo.setDefinitionName(pi.getProcessDefinitionName());

        return processInstanceBo;
    }

    public static List<ProcessInstanceBo> fromHistory(List<HistoricProcessInstance> list) {
        List<ProcessInstanceBo> ret = new ArrayList<>();
        for (HistoricProcessInstance pi : list) {
            ret.add(ProcessInstanceBo.fromHistory(pi));
        }
        return ret;
    }
}
