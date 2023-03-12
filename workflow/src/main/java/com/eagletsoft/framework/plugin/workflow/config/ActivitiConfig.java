package com.eagletsoft.framework.plugin.workflow.config;

import com.eagletsoft.framework.plugin.workflow.custom.CustomUserTaskParseHandler;
import com.eagletsoft.framework.plugin.workflow.custom.CustomerProcessParseHandler;
import org.activiti.engine.parse.BpmnParseHandler;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ActivitiConfig {

    @Autowired(required = false)
    private SpringProcessEngineConfiguration processEngineConfiguration;

    @PostConstruct
    private void init() {
        List<BpmnParseHandler> customHandlers = new ArrayList<>();
        customHandlers.add(new CustomUserTaskParseHandler());
        customHandlers.add(new CustomerProcessParseHandler());
        processEngineConfiguration.setCustomDefaultBpmnParseHandlers(customHandlers);
    }
}
