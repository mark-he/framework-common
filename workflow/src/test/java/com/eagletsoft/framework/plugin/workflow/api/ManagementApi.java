package com.eagletsoft.framework.plugin.workflow.api;

import com.eagletsoft.boot.framework.api.utils.ApiResponse;
import com.eagletsoft.framework.plugin.workflow.ProcessManager;
import com.eagletsoft.framework.plugin.workflow.WorkflowManager;
import com.eagletsoft.framework.plugin.workflow.data.ProcessInstanceBo;
import com.eagletsoft.framework.plugin.workflow.interfaces.ProcessDefinitionQueryReq;
import com.eagletsoft.framework.plugin.workflow.interfaces.StartProcessReq;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/management")
public class ManagementApi {
    @PostMapping(value = "/workflow/search")
    public @ResponseBody ApiResponse search(@RequestBody ProcessDefinitionQueryReq req) {
        Object obj = WorkflowManager.getInstance().findLatestProcesses(null, req);
        return ApiResponse.make(obj);
    }

}
