package com.eagletsoft.framework.plugin.workflow.custom;

import org.activiti.bpmn.model.*;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class CustomUserTaskParseHandler extends UserTaskParseHandler {

    @Override
    protected void executeParse(BpmnParse bpmnParse, UserTask userTask) {
        super.executeParse(bpmnParse, userTask);
        ActivitiListener listener = new ActivitiListener();
        listener.setEvent(GeneralTaskListener.EVENTNAME_ALL_EVENTS);

        listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        listener.setImplementation("com.eagletsoft.framework.plugin.workflow.custom.GeneralTaskListener");
        userTask.getTaskListeners().add(listener);

        if (userTask.getCandidateUsers().isEmpty()) {
            userTask.getCandidateUsers().add("");
        }

        UserExpressionUtils.applyUsers(userTask.getCandidateUsers());

        if (!userTask.getCandidateGroups().isEmpty()) {
            UserExpressionUtils.applyGroups(userTask.getCandidateGroups());
            userTask.getCandidateUsers().addAll(userTask.getCandidateGroups());
            userTask.getCandidateGroups().clear();
        }
    }
}
