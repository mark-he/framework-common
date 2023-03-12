package com.eagletsoft.framework.plugins.processchain;

public interface IExecutor<T extends ProcessContext> {
    boolean execute(IProcess process, T context);
}
