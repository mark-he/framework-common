package com.eagletsoft.framework.plugin.dataview.def.functions;

public interface IFunction {

    String getName();
    Object execute(Object root, Object param);
}
