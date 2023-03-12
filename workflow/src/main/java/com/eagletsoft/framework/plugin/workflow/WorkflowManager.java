package com.eagletsoft.framework.plugin.workflow;

import com.eagletsoft.boot.framework.common.errors.ServiceException;
import com.eagletsoft.boot.framework.common.errors.StandardErrors;
import com.eagletsoft.framework.plugin.workflow.data.ProcessDefinitionBo;
import com.eagletsoft.framework.plugin.workflow.image.ImageGenerator;
import com.eagletsoft.framework.plugin.workflow.interfaces.DeployReq;
import com.eagletsoft.framework.plugin.workflow.interfaces.PageResult;
import com.eagletsoft.framework.plugin.workflow.interfaces.ProcessDefinitionQueryReq;
import com.eagletsoft.framework.plugin.workflow.security.SecurityAssertion;
import com.eagletsoft.framework.plugin.workflow.security.impl.DefaultSecurityAssertion;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipInputStream;

public class WorkflowManager {
    private static Logger LOG = LoggerFactory.getLogger(WorkflowManager.class);

    private static WorkflowManager INSTANCE = new WorkflowManager();
    private SecurityAssertion securityAssertion = new DefaultSecurityAssertion();

    private WorkflowManager() {}

    public static WorkflowManager getInstance() {
        return INSTANCE;
    }

    private ProcessDefinition findLatestProcesses(String deploymentId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        query.deploymentId(deploymentId)
                .latestVersion();
        ProcessDefinition def = query.singleResult();
        return def;
    }

    public ProcessDefinitionBo createDeploymentXML(String tenantId, DeployReq req) {
        this.securityAssertion.assertManage(tenantId);

        try {
            BpmnXMLConverter c = new BpmnXMLConverter();
            XMLInputFactory xif = XMLInputFactory.newInstance();
            //InputStream in = WorkflowManager.class.getClassLoader().getResourceAsStream(req.getFile());

            byte[] bytes = Base64.decode(req.getFile());
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);

            XMLStreamReader reader = xif.createXMLStreamReader(in);
            BpmnModel model = c.convertToBpmnModel(reader);


            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            Deployment deployment = processEngine.getRepositoryService()
                    .createDeployment()
                    .tenantId(tenantId)
                    .name(req.getName())
                    .category(req.getCategory())
                    .addBpmnModel(req.getName() + ".bpmn", model)
                    .addInputStream("xml", new ByteArrayInputStream(bytes))
                    .deploy();
            ProcessDefinition def = this.findLatestProcesses(deployment.getId());

            return ProcessDefinitionBo.from(def);
        } catch (Exception ex) {
            LOG.error("Error in deployment", ex);
            throw new ServiceException(StandardErrors.CLIENT_ERROR.getStatus(), "error.workflow.deploy", ex.getMessage());
        }
    }

    public BpmnModel findBpmnModel(String id) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(id);
        return bpmnModel;
    }

    public ProcessDefinitionBo createDeploymentZIP(String tenantId, DeployReq req) {
        this.securityAssertion.assertManage(tenantId);

        try {

            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

            //InputStream in = WorkflowManager.class.getClassLoader().getResourceAsStream(req.getFile());
            byte[] bytes = Base64.decode(req.getFile());
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);

            ZipInputStream zipInputStream = new ZipInputStream(in);

            Deployment deployment = processEngine.getRepositoryService()
                    .createDeployment()
                    .tenantId(tenantId)
                    .name(req.getName())
                    .category(req.getCategory())
                    .addZipInputStream(zipInputStream)
                    .deploy();
            ProcessDefinition def = this.findLatestProcesses(deployment.getId());

            return ProcessDefinitionBo.from(def);
        } catch (Exception ex) {
            LOG.error("Error in deployment", ex);
            throw new ServiceException(StandardErrors.CLIENT_ERROR.getStatus(), "error.workflow.deploy", ex.getMessage());
        }
    }

    public void deleteDeployment(String tenantId, String id) {
        this.securityAssertion.assertManage(tenantId);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        Deployment deployment = processEngine.getRepositoryService().createDeploymentQuery().deploymentId(id).singleResult();
        if (null != deployment && tenantId.equals(deployment.getTenantId())) {
            processEngine.getRepositoryService().deleteDeployment(id);
        }
        //TODO throws not-found Exception if necessary.
    }

    public ProcessDefinitionBo findProcessByKey(String tenantId, String key) {
        this.securityAssertion.assertRead(tenantId);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessDefinition def = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .processDefinitionTenantId(tenantId)
                .latestVersion()
                .singleResult();

        return ProcessDefinitionBo.from(def);
    }

    public ProcessDefinitionBo findProcessById(String tenantId, String id) {
        this.securityAssertion.assertRead(tenantId);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessDefinition def = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(id)
                .processDefinitionTenantId(tenantId)
                .singleResult();

        return ProcessDefinitionBo.from(def);
    }

    public void deleteProcessByKey(String tenantId, String key) {
        this.securityAssertion.assertManage(tenantId);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<ProcessDefinition> list = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .list();

        for (ProcessDefinition def : list) {
            if (tenantId.equals(def.getTenantId())) {
                processEngine.getRepositoryService().deleteDeployment(def.getDeploymentId());
            }
        }
    }

    public PageResult findLatestProcesses(String tenantId, ProcessDefinitionQueryReq req) {
        this.securityAssertion.assertRead(tenantId);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();

        if (null != tenantId) {
            query.processDefinitionTenantId(tenantId);
        }

        if (!req.isIncludeSuspended()) {
            query.active();
        }

        query.latestVersion()
                .orderByProcessDefinitionName()
                .asc();

        PageResult pr = PageResult.make(query, req);
        pr.setData(ProcessDefinitionBo.from(pr.getData()));
        return pr;
    }

    public InputStream generateImage(String tenantId, String id) throws Exception {
        this.securityAssertion.assertRead(tenantId);
        ProcessDefinitionBo def = findProcessById(tenantId, id);
        if (null != def) {
            return ImageGenerator.generateDef(id);
        }
        return null;
    }

    public InputStream generateImageByKey(String tenantId, String key) throws Exception {
        this.securityAssertion.assertRead(tenantId);
        ProcessDefinitionBo def = findProcessByKey(tenantId, key);
        if (null != def) {
            return ImageGenerator.generateDef(def.getId());
        }
        return null;
    }

    public Collection<FlowElement> getActivities(String tenantId, String id) {
        this.securityAssertion.assertRead(tenantId);
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        ProcessDefinitionBo def = this.findProcessById(tenantId, id);
        if (null != def) {
            BpmnModel model = processEngine.getRepositoryService().getBpmnModel(id);
            Collection<FlowElement> ret = null;
            if(model != null) {
                ret = model.getMainProcess().getFlowElements();
            }
            return ret;
        }
        return null;
    }
}
