package com.eagletsoft.framework.plugin.dataview.def.functions;

import ognl.Ognl;

import java.util.HashMap;
import java.util.Map;

public class ExpressionPresenter {

    public ExpressionPresenter() {
    }

    public static Object apply(String expression, Object root) {
        Object ret = null;
        if (expression.charAt(0) == '#') {
            int lc = expression.indexOf('(');
            int rc = expression.indexOf(')');
            String function = expression.substring(1, lc);
            String param = expression.substring(lc + 1, rc);
            Object paramValue = readValue(param, root);

            if (null != paramValue) {
                ret = FunctionRegister.getInstance().find(function).execute(root, paramValue);
                String more = expression.substring(rc + 1);
                if (more.length() > 0 && more.startsWith(".")) {
                    more = "this" + more;
                    Map<String, Object> sub = new HashMap<>();
                    sub.put("this", ret);

                    ret = readValue(more, sub);
                }
            }
        }
        else {
            ret = readValue(expression, root);
        }
        return ret;
    }

    private static Object readValue(String expression, Object root)  {
        try {
            Object ret = Ognl.getValue(expression, root);
            return ret;
        }
        catch (Exception ex) {
            return null;
        }
    }
}

