package com.eagletsoft.framework.plugins.processchain.impl;

import com.eagletsoft.boot.framework.common.errors.ServiceException;
import com.eagletsoft.boot.framework.common.errors.StandardErrors;
import com.eagletsoft.boot.framework.common.utils.ApplicationUtils;
import com.eagletsoft.boot.framework.common.utils.ExpressionUtils;
import com.eagletsoft.framework.plugins.processchain.IExecutor;
import com.eagletsoft.framework.plugins.processchain.IProcess;
import com.eagletsoft.framework.plugins.processchain.ProcessContext;
import com.eagletsoft.framework.plugins.processchain.meta.Activity;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
@Activity(name = "start", executor = Void.class, events = {
        "x = 1,start2"
})
@Activity(name = "start2", executor = Void.class, note = "")
 */
public class AbstractProcess<T extends ProcessContext> implements IProcess<T> {

    protected T context;
    private ActivityNode currentNode;
    private String startActivity = "start";
    private Map<String, ActivityNode> activityMap = new HashMap<>();

    public AbstractProcess(T context) {
        this.context = context;
        this.init();
    }

    public AbstractProcess(T context, String startActivity) {
        this.context = context;
        this.startActivity = startActivity;
        this.init();
    }

    public void init() {
        Activity[] activities = this.getClass().getAnnotationsByType(Activity.class);

        for(Activity activity : activities) {
            if (activityMap.containsKey(activity.name())) {
                throw new RuntimeException("Duplicated activity names in Process " + this.getClass().getName());
            }

            String[] events = activity.events();
            List<FlowSequence> flowSequences = new ArrayList<>(events.length);
            ActivityNode node = new ActivityNode(activity.name(), activity.executor(), flowSequences, activity.callback(), activity.executorMethod());

            for (String event : events) {
                String[] arr = event.split(",", -1);
                String e = null;
                String next = null;
                if (arr.length == 1) {
                    e = "";
                    next = arr[0];
                } else if (arr.length == 2) {
                    e = arr[0];
                    next = arr[1];
                }
                else {
                    throw new RuntimeException("Error event defined in Process " + this.getClass().getName());
                }

                flowSequences.add(new FlowSequence(e.trim(), next.trim()));
            }

            activityMap.put(activity.name(), node);
        }
    }

    @Override
    public T getContext() {
        return context;
    }

    @Override
    public void callback(Map processVariables) {
        currentNode = null;
        for (ActivityNode activityNode : activityMap.values()) {
            if (StringUtils.isNotEmpty(activityNode.callback)) {
                Object ret = ExpressionUtils.readValue(activityNode.callback, processVariables);
                if (null == ret || !(ret instanceof Boolean)) {
                    throw new RuntimeException("Error expression");
                } else {
                    if (((Boolean) ret).booleanValue()) {
                        currentNode = null;
                        startActivity = activityNode.name;
                        this.next();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void next() {
        String nextActivity = null;
        if (null == currentNode) {
            nextActivity = startActivity;
        }
        else {

            for (FlowSequence fs : currentNode.flowSequences) {
                if (StringUtils.isEmpty(fs.getExp())) {
                    if (null == nextActivity) {
                        nextActivity = fs.getNext();
                    }
                } else {
                    Object ret = ExpressionUtils.readValue(fs.getExp(), context);
                    if (null == ret || !(ret instanceof Boolean)) {
                        throw new RuntimeException("Error expression");
                    } else {
                        if (((Boolean) ret).booleanValue()) {
                            nextActivity = fs.getNext();
                            break;
                        }
                    }
                }
            }
        }
        if (null != nextActivity) {
            ActivityNode nextNode = activityMap.get(nextActivity);
            if (null == nextNode) {
                throw new RuntimeException("Missing node for activity " + nextActivity);
            }

            currentNode = nextNode;
            boolean ret = false;
            if (StringUtils.isEmpty(currentNode.executorMethod)) {
                IExecutor activity = this.findActivity(currentNode.executor);
                ret = activity.execute(this, this.getContext());
            } else {
                executeMethod(currentNode.executorMethod);
                ret = true;
            }

            if (ret) {
                this.next();
            }
        } else {
            currentNode = null;
        }
    }

    protected void executeMethod(String name) {
        Method method = null;
        try {
            method = this.getClass().getMethod(name);
            method.invoke(this);
        }
        catch (InvocationTargetException ite) {
            if (ite.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)ite.getTargetException();
            } else {
                throw new RuntimeException(ite);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected IExecutor findActivity(Class<IExecutor> clazz) {
        IExecutor activity = ApplicationUtils.getBean(clazz, false);
        if (null == activity) {
            try {
                activity = clazz.newInstance();
            } catch (Exception ex) {
                throw new ServiceException(StandardErrors.INTERNAL_ERROR.getStatus(), String.format("%s should have a default constructor", clazz.getName()));
            }
        }
        return activity;
    }

    public static class ActivityNode {
        private String name;
        private String callback;
        private List<FlowSequence>  flowSequences;
        private Class executor;
        private String executorMethod;

        public ActivityNode(String name, Class executor, List<FlowSequence> flowSequences, String callback, String executorMethod) {
            this.name = name;
            this.flowSequences = flowSequences;
            this.executor = executor;
            this.callback = callback;
            this.executorMethod = executorMethod;
        }
    }
}
