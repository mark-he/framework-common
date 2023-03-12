package com.eagletsoft.framework.plugin.workflow.integrate;

import com.eagletsoft.framework.plugin.workflow.data.ProcessInstanceBo;

import java.util.List;

public interface UserProvider {
    List<String> withGroup(ProcessInstanceBo pi, String... groups);


    List<String> withName(ProcessInstanceBo pi, String... names);
}
