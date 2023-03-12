package com.eagletsoft.framework.plugin.workflow.data;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeploymentBo {
    private String id;
    private String key;
    private String name;
    private Date timestamp;
    private String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public static DeploymentBo from(Deployment deployment) {
        if (null == deployment) {
            return null;
        }

        DeploymentBo deploymentBo = new DeploymentBo();
        deploymentBo.setId(deployment.getId());
        deploymentBo.setKey(deployment.getKey());
        deploymentBo.setName(deployment.getName());
        deploymentBo.setTimestamp(deployment.getDeploymentTime());
        deploymentBo.setTenantId(deployment.getTenantId());
        return deploymentBo;
    }

    public static List<DeploymentBo> from(List<Deployment> list) {
        List<DeploymentBo> ret = new ArrayList<>();
        for (Deployment deployment : list) {
            ret.add(DeploymentBo.from(deployment));
        }
        return ret;
    }
}
