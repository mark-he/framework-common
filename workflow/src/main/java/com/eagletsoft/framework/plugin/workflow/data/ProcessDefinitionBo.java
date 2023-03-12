package com.eagletsoft.framework.plugin.workflow.data;

import org.activiti.engine.repository.ProcessDefinition;
import java.util.ArrayList;
import java.util.List;

public class ProcessDefinitionBo {
    private String deploymentId;
    private String category;
    private String name;
    private String id;
    private Integer version;
    private String tenantId;
    private String description;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static ProcessDefinitionBo from(ProcessDefinition def) {
        if (null == def) {
            return null;
        }
        ProcessDefinitionBo processDefinitionBo = new ProcessDefinitionBo();

        processDefinitionBo.setDeploymentId(def.getDeploymentId());
        processDefinitionBo.setId(def.getId());
        processDefinitionBo.setName(def.getName());
        processDefinitionBo.setVersion(def.getVersion());
        processDefinitionBo.setTenantId(def.getTenantId());
        processDefinitionBo.setCategory(def.getCategory());
        processDefinitionBo.setDescription(def.getDescription());
        processDefinitionBo.setKey(def.getKey());

        return processDefinitionBo;
    }

    public static List<ProcessDefinitionBo> from(List<ProcessDefinition> list) {
        List<ProcessDefinitionBo> ret = new ArrayList<>();
        for (ProcessDefinition def : list) {
            ret.add(ProcessDefinitionBo.from(def));
        }
        return ret;
    }
}
