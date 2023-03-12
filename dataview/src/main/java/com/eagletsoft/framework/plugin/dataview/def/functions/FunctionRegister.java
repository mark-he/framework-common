package com.eagletsoft.framework.plugin.dataview.def.functions;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class FunctionRegister {
    public final static FunctionRegister INSTANCE = new FunctionRegister();
    private FunctionRegister() {
        init();
    }

    public static FunctionRegister getInstance() {
        return INSTANCE;
    }

    private Map<String, IFunction> functionMap = new HashMap<>();

    private void init() {
        this.add(new CurrentUser());
        this.add(new CurrentTimestamp());
        this.add(new Uuid());
    }

    public void add(IFunction fun) {
        functionMap.put(fun.getName(), fun);
    }

    public Map<String, IFunction> getFunctionMap() {
        return functionMap;
    }

    public void setFunctionMap(Map<String, IFunction> functionMap) {
        this.functionMap = functionMap;
    }

    public IFunction find(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        IFunction fun = functionMap.get(name);

        if (null == fun) {
            throw new RuntimeException("Error config with function name: " + name);
        }
        return fun;
    }
}
