package com.eagletsoft.framework.plugin.dataview.def.functions;

import java.util.Date;

public class CurrentTimestamp implements IFunction {

    @Override
    public String getName() {
        return "current_timestamp";
    }

    @Override
    public Object execute(Object root, Object param) {
        return new Date();
    }
}
