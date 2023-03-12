package com.eagletsoft.framework.plugin.dataview.def.functions;

import com.eagletsoft.boot.framework.common.session.UserSession;

public class CurrentUser implements IFunction {

    @Override
    public String getName() {
        return "current_user";
    }

    @Override
    public Object execute(Object root, Object param) {
        return UserSession.getUserInterface();
    }


}
