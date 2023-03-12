package com.eagletsoft.framework.plugins.statemachine.impl;


import com.eagletsoft.boot.framework.common.errors.ServiceException;
import com.eagletsoft.boot.framework.common.errors.StandardErrors;
import com.eagletsoft.boot.framework.common.utils.ApplicationUtils;
import com.eagletsoft.boot.framework.common.utils.ExpressionUtils;
import com.eagletsoft.framework.plugins.statemachine.IExecutor;
import com.eagletsoft.framework.plugins.statemachine.IStateMachine;
import com.eagletsoft.framework.plugins.statemachine.meta.Trigger;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
/*
@Trigger(value = "", event = "", next = "")
 */
public abstract class AbstractStateMachine<T> implements IStateMachine<T> {

    private Map<String, Map<String, TriggerNode>> triggerMap = new HashMap<>();

    protected T target;
    public AbstractStateMachine(T target) {
        this.target = target;
        this.init();
    }

    @Override
    public T getTarget() {
        return target;
    }


    @Override
    public boolean test(String event, Object data) {
        Map<String, TriggerNode> map = triggerMap.get(this.currentState());
        if (null == map) {
            return false;
        }

        TriggerNode node = map.get(event);
        if (null == node) {
            return false;
        }

        if (StringUtils.isNotEmpty(node.getCriteria())) {
            Object ret = ExpressionUtils.readValue(node.criteria, target);
            if (null == ret || !(ret instanceof Boolean)) {
                return false;
            } else {
                if (!((Boolean) ret).booleanValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String turn(String event, Object data) {
        Map<String, TriggerNode> map = triggerMap.get(this.currentState());
        if (null == map) {
            throw new ServiceException(StandardErrors.INTERNAL_ERROR.getStatus(), "error.state.wrong");
        }

        TriggerNode node = map.get(event);
        if (null == node) {
            throw new ServiceException(StandardErrors.INTERNAL_ERROR.getStatus(), "error.state.wrong.event");
        }

        if (StringUtils.isNotEmpty(node.getCriteria())) {
            Object ret = ExpressionUtils.readValue(node.criteria, target);
            if (null == ret || !(ret instanceof Boolean)) {
                throw new RuntimeException("Error expression");
            } else {
                if (!((Boolean) ret).booleanValue()) {
                    String message = node.message;
                    if (StringUtils.isEmpty(message)) {
                        message = "error.state.event.criteria";
                    }
                    throw new ServiceException(StandardErrors.INTERNAL_ERROR.getStatus(), message);
                }
            }
        }

        if (null != node.getExecutor() && !node.getExecutor().equals(Void.class)) {
            IExecutor executor = this.findExecutor(node.getExecutor());
            executor.execute(this, event, data);
        }

        this.updateNextState(node.getNext());
        return node.getNext();
    }

    protected IExecutor findExecutor(Class<IExecutor> clazz) {
        IExecutor executor = ApplicationUtils.getBean(clazz, false);
        if (null == executor) {
            try {
                executor = clazz.newInstance();
            } catch (Exception ex) {
                throw new ServiceException(StandardErrors.INTERNAL_ERROR.getStatus(), String.format("%s should have a default constructor", clazz.getName()));
            }
        }
        return executor;
    }


    public void init() {
        Trigger[] triggers = this.getClass().getAnnotationsByType(Trigger.class);
        for (Trigger trigger : triggers) {
            TriggerNode node = new TriggerNode(trigger.value(), trigger.event(), trigger.next(), trigger.executor(), trigger.criteria(), trigger.message());
            Map<String, TriggerNode> map = triggerMap.get(trigger.value());
            if (null == map) {
                map = new HashMap<>();
                triggerMap.put(trigger.value(), map);
            }
            map.put(trigger.event(), node);
        }
    }

    public static class TriggerNode {
        private String name;
        private String event;
        private String next;
        private String criteria;
        private Class executor;
        private String message;

        public TriggerNode(String name, String event, String next, Class executor, String criteria, String message) {
            this.name = name;
            this.event = event;
            this.next = next;
            this.executor = executor;
            this.criteria = criteria;
            this.message = message;
        }

        public Class getExecutor() {
            return executor;
        }

        public void setExecutor(Class executor) {
            this.executor = executor;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getCriteria() {
            return criteria;
        }

        public void setCriteria(String criteria) {
            this.criteria = criteria;
        }
    }
}
