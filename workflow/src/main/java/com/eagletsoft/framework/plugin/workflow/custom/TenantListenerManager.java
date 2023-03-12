package com.eagletsoft.framework.plugin.workflow.custom;

import com.eagletsoft.framework.plugin.workflow.integrate.ProcessListener;

import java.util.HashMap;
import java.util.Map;

public class TenantListenerManager {
    private final static TenantListenerManager INSTANCE = new TenantListenerManager();
    private Map<String, ProcessListener> listenerMap = new HashMap<>();

    public static TenantListenerManager getInstance() {
        return INSTANCE;
    }

    public ProcessListener get(Object key) {
        return listenerMap.get(key);
    }

    public ProcessListener put(String key, ProcessListener value) {
        return listenerMap.put(key, value);
    }

    public ProcessListener remove(Object key) {
        return listenerMap.remove(key);
    }
}
