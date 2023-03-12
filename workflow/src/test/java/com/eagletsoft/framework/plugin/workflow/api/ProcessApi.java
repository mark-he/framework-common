package com.eagletsoft.framework.plugin.workflow.api;

import com.eagletsoft.boot.framework.api.utils.ApiResponse;
import com.eagletsoft.framework.plugin.workflow.ProcessManager;
import com.eagletsoft.framework.plugin.workflow.UserProcessManager;
import com.eagletsoft.framework.plugin.workflow.data.TaskDetailBo;
import com.eagletsoft.framework.plugin.workflow.interfaces.*;
import com.eagletsoft.framework.plugin.workflow.data.ProcessInstanceBo;
import com.eagletsoft.framework.plugin.workflow.data.TaskBo;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping(value = "/process")
public class ProcessApi {
    @PostMapping(value = "/{tenantId}/start")
    public @ResponseBody ApiResponse start(@PathVariable String tenantId, @RequestBody StartProcessReq req) {
        ProcessInstanceBo pi = UserProcessManager.getInstance().start(tenantId, req);
        return ApiResponse.make(pi);
    }

    @PostMapping(value = "/{tenantId}/{id}/active")
    public @ResponseBody ApiResponse active(@PathVariable String tenantId, @PathVariable String id) {
        UserProcessManager.getInstance().active(tenantId, id);
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/{id}/suspend")
    public @ResponseBody ApiResponse suspend(@PathVariable String tenantId, @PathVariable String id) {
        UserProcessManager.getInstance().suspend(tenantId, id);
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/{id}/terminate")
    public @ResponseBody ApiResponse terminate(@PathVariable String tenantId, @PathVariable String id, @RequestBody TerminateProcessReq req) {
        UserProcessManager.getInstance().terminate(tenantId, id, req);
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/{id}/image")
    public void generateImage(@PathVariable String tenantId, @PathVariable String id, HttpServletResponse response) throws Exception {
        InputStream in = UserProcessManager.getInstance().generateImage(tenantId, id);
        if (null != in) {
            response.setContentType("image/png");
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @PostMapping(value = "/{tenantId}/search/startedBy")
    public @ResponseBody ApiResponse startedBy(@PathVariable String tenantId, @RequestBody ProcessInstanceQueryReq req) {
        Object ret = UserProcessManager.getInstance().searchMyStarted(tenantId, req);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/search")
    public @ResponseBody ApiResponse search(@PathVariable String tenantId, @RequestBody ProcessInstanceQueryReq req) {
        TaskQueryReq  request = new TaskQueryReq();
        request.setProcessDefinitionKey(req.getProcessDefinitionKey());
//        Object ret = UserProcessManager.getInstance().searchMyInvolved(tenantId, req);
        Object ret = UserProcessManager.getInstance().searchHistoricTasks(tenantId, request);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/find/businessKey/{id}")
    public @ResponseBody ApiResponse findByBusinessKey(@PathVariable String tenantId, @PathVariable String id) {
        List<ProcessInstanceBo> ret = ProcessManager.getInstance().findByBusinessKey(tenantId, id);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/{id}")
    public @ResponseBody ApiResponse findById(@PathVariable String tenantId, @PathVariable String id) {
        ProcessInstanceBo ret = ProcessManager.getInstance().findById(tenantId, id);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/{id}/tasks")
    public @ResponseBody ApiResponse findTasks(@PathVariable String tenantId, @PathVariable String id) {
        List<TaskDetailBo> ret = ProcessManager.getInstance().findTaskDetailsByProcessInstance(tenantId, id);
        return ApiResponse.make(ret);
    }

}
