package com.eagletsoft.framework.plugin.workflow.api;

import com.eagletsoft.boot.framework.api.utils.ApiResponse;
import com.eagletsoft.framework.plugin.workflow.WorkflowManager;
import com.eagletsoft.framework.plugin.workflow.data.ProcessDefinitionBo;
import com.eagletsoft.framework.plugin.workflow.interfaces.DeployReq;
import com.eagletsoft.framework.plugin.workflow.interfaces.ProcessDefinitionQueryReq;
import org.activiti.bpmn.model.FlowElement;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Collection;

@Controller
@RequestMapping(value = "/workflow")
public class WorkflowApi {
    @PostMapping(value = "/{tenantId}/deployment/createWithZIP")
    public @ResponseBody ApiResponse createDeployment(@PathVariable String tenantId, @RequestBody DeployReq req) {
        Object obj = WorkflowManager.getInstance().createDeploymentZIP(tenantId, req);
        return ApiResponse.make(obj);
    }

    @PostMapping(value = "/{tenantId}/deployment/createWithXML")
    public @ResponseBody ApiResponse createDeploymentWithDefault(@PathVariable String tenantId, @RequestBody DeployReq req) {
        Object obj =  WorkflowManager.getInstance().createDeploymentXML(tenantId, req);
        return ApiResponse.make(obj);
    }

    @PostMapping(value = "/{tenantId}/deployment/{id}/delete")
    public @ResponseBody ApiResponse deleteDeployment(@PathVariable String tenantId, @PathVariable String id) {
        WorkflowManager.getInstance().deleteDeployment(tenantId, id);
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/process/delete/{key}")
    public @ResponseBody ApiResponse deleteProcess(@PathVariable String tenantId, @PathVariable String key) {
        WorkflowManager.getInstance().deleteProcessByKey(tenantId, key);
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/process/search")
    public @ResponseBody ApiResponse search(@PathVariable String tenantId, @RequestBody ProcessDefinitionQueryReq req) {
        Object ret = WorkflowManager.getInstance().findLatestProcesses(tenantId, req);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/process/{id}/activities")
    public @ResponseBody ApiResponse getActivities(@PathVariable String tenantId, @PathVariable String id) {
        Collection<FlowElement> ret = WorkflowManager.getInstance().getActivities(tenantId, id);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/process/key/{key}")
    public @ResponseBody ApiResponse findByKey(@PathVariable String tenantId, @PathVariable String key) {
        ProcessDefinitionBo ret = WorkflowManager.getInstance().findProcessByKey(tenantId, key);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/process/{id}")
    public @ResponseBody ApiResponse findById(@PathVariable String tenantId, @PathVariable String id) {
        ProcessDefinitionBo ret = WorkflowManager.getInstance().findProcessById(tenantId, id);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/process/{id}/image")
    public void getImage(@PathVariable String tenantId, @PathVariable String id, HttpServletResponse response) throws Exception {
        InputStream in = WorkflowManager.getInstance().generateImage(tenantId, id);
        if (null != in) {
            response.setContentType("image/png");
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @PostMapping(value = "/{tenantId}/process/key/{key}/image")
    public void getImageByKey(@PathVariable String tenantId, @PathVariable String key, HttpServletResponse response) throws Exception {
        InputStream in = WorkflowManager.getInstance().generateImageByKey(tenantId, key);
        if (null != in) {
            response.setContentType("image/png");
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        }
    }
}
