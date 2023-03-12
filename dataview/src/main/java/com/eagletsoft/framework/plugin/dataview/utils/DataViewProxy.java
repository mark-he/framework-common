package com.eagletsoft.framework.plugin.dataview.utils;

import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DataViewProxy implements InvocationHandler {
    private Object target;

    public DataViewProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        String property = findGetProperty(method);
        if (!StringUtils.isEmpty(property)) {
            Map<String, Field> fieldMap = BeanUtils.findAllDeclaredFieldMap(o.getClass());
            Field field = fieldMap.get(property);
            if (null != field) {
                return DataFieldUtils.normalize(target, field);
            }
        }
        return method.invoke(target, objects);
    }

    public static void proxy(List objects) {
        for (int i = 0; i < objects.size(); i++) {
            objects.set(i, proxy(objects.get(i)));
        }
    }

    public static void proxy(Object[] objects) {
        for (int i = 0; i < objects.length; i++) {
            objects[i] = proxy(objects[i]);
        }
    }

    public static <T> T proxy(T object) {
        if (null != object) {
            DataView dv = object.getClass().getAnnotation(DataView.class);

            if (null == dv || object instanceof DataViewProxy) {
                return object;
            }
            else {
                return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(),
                        object.getClass().getInterfaces(),
                        new DataViewProxy(object));
            }
        }
        else {
            return null;
        }
    }

    private String findGetProperty(Method method) {
        if (method.getName().startsWith("get")) {
            return method.getName().substring(3);
        }
        else if (method.getName().startsWith("is")) {
            return method.getName().substring(2);
        }
        return null;
    }
}
