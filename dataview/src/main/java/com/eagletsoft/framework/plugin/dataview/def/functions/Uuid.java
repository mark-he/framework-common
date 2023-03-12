package com.eagletsoft.framework.plugin.dataview.def.functions;

import com.eagletsoft.boot.framework.common.utils.UuidUtils;

public class Uuid implements IFunction {

    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public Object execute(Object root, Object param) {
        return UuidUtils.getUUID();
    }
}
