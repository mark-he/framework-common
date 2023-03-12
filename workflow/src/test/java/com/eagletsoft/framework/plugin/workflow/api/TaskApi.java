package com.eagletsoft.framework.plugin.workflow.api;

import com.eagletsoft.boot.framework.api.utils.ApiResponse;
import com.eagletsoft.framework.plugin.workflow.ProcessManager;
import com.eagletsoft.framework.plugin.workflow.UserProcessManager;
import com.eagletsoft.framework.plugin.workflow.data.TaskBo;
import com.eagletsoft.framework.plugin.workflow.data.TaskDetailBo;
import com.eagletsoft.framework.plugin.workflow.interfaces.CompleteTaskReq;
import com.eagletsoft.framework.plugin.workflow.interfaces.DelegateTaskReq;
import com.eagletsoft.framework.plugin.workflow.interfaces.TaskQueryReq;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/task")
public class TaskApi {
    @PostMapping(value = "/{tenantId}/assigned")
    public @ResponseBody
    ApiResponse assigned(@PathVariable String tenantId, @RequestBody TaskQueryReq req) {
        List<TaskBo> ret = UserProcessManager.getInstance().findTasks(tenantId, req, true);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/search")
    public @ResponseBody ApiResponse search(@PathVariable String tenantId, @RequestBody TaskQueryReq req) {
        Object ret = UserProcessManager.getInstance().searchTasks(tenantId, req);
        return ApiResponse.make(ret);
    }

    @PostMapping(value = "/{tenantId}/{id}/complete")
    public @ResponseBody ApiResponse complete(@PathVariable String tenantId, @PathVariable String id, @RequestBody CompleteTaskReq req) {
        UserProcessManager.getInstance().completeTask(tenantId, id, req);
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/{id}/claim")
    public @ResponseBody ApiResponse claim(@PathVariable String tenantId, @PathVariable String id) {
        UserProcessManager.getInstance().claimTask(tenantId, id);
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/{id}/unclaim")
    public @ResponseBody ApiResponse unclaim(@PathVariable String tenantId, @PathVariable String id) {
        UserProcessManager.getInstance().unclaimTask(tenantId, id);
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/{id}/delegate")
    public @ResponseBody ApiResponse delegate(@PathVariable String tenantId, @PathVariable String id, @RequestBody DelegateTaskReq req) {
        UserProcessManager.getInstance().delegateTask(tenantId, id, req.getDelegateUserId());
        return ApiResponse.make();
    }

    @PostMapping(value = "/{tenantId}/{id}")
    public @ResponseBody ApiResponse find(@PathVariable String tenantId, @PathVariable String id) {
        TaskDetailBo ret = UserProcessManager.getInstance().findTaskDetail(tenantId, id);
        return ApiResponse.make(ret);
    }
}
