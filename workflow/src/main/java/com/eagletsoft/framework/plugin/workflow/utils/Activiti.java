package com.eagletsoft.framework.plugin.workflow.utils;

import com.eagletsoft.framework.plugin.workflow.data.ProcessInstanceBo;
import com.eagletsoft.framework.plugin.workflow.integrate.UserProvider;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("activiti")
public class Activiti {
    @Autowired(required = false)
    private UserProvider userProvider;

    public List<String> groupUsers(DelegateExecution execution, String... groups) {
        if (null != userProvider) {
            if (execution instanceof ExecutionEntityImpl) {
                ExecutionEntityImpl impl = (ExecutionEntityImpl)execution;
                return userProvider.withGroup(ProcessInstanceBo.from(impl.getProcessInstance()), groups);
            }
        }
        return Collections.emptyList();
    }

    public List<String> users(DelegateExecution execution, String... names) {
        if (null != userProvider) {
            if (execution instanceof ExecutionEntityImpl) {
                ExecutionEntityImpl impl = (ExecutionEntityImpl)execution;
                return userProvider.withName(ProcessInstanceBo.from(impl.getProcessInstance()), names);
            }
        }
        return Collections.emptyList();
    }

    public UserProvider getUserProvider() {
        return userProvider;
    }

    public void setUserProvider(UserProvider userProvider) {
        this.userProvider = userProvider;
    }
}
