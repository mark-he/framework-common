package com.eagletsoft.framework.plugin.workflow.custom;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.ProcessParseHandler;

public class CustomerProcessParseHandler extends ProcessParseHandler {
    @Override
    protected void executeParse(BpmnParse bpmnParse, Process process) {
        super.executeParse(bpmnParse, process);

        String[] events = {GeneralExecutionListener.EVENTNAME_START, GeneralExecutionListener.EVENTNAME_TAKE, GeneralExecutionListener.EVENTNAME_END};
        for (String event : events) {
            ActivitiListener listener = new ActivitiListener();
            listener.setEvent(event);
            //listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
            //listener.setImplementation("#{activiti.onProcess(execution)}");
            listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
            listener.setImplementation("com.eagletsoft.framework.plugin.workflow.custom.GeneralExecutionListener");
            process.getExecutionListeners().add(listener);
        }

        UserExpressionUtils.applyUsers(process.getCandidateStarterUsers());
    }
}
