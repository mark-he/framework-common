package com.eagletsoft.framework.plugins.processchain;

import java.util.Map;

public interface IProcess<T extends ProcessContext> {
    T getContext();
    void next();
    void callback(Map processVariables);
}
