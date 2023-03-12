package com.eagletsoft.framework.plugin.dataview.validator.impl;

import com.eagletsoft.framework.plugin.dataview.def.meta.Dependency;
import com.eagletsoft.framework.plugin.dataview.validator.IDependencyValidator;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataFieldViolation;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class DependencyValidator implements IDependencyValidator {

    @Override
    public DataViolation validate(Dependency dep, Object bean, Field field) {
        DataViolation dv = null;
        try {
            Object current = PropertyUtils.getProperty(bean, field.getName());
            Object onObj = PropertyUtils.getProperty(bean, dep.on());

            String currentStrValue = "";
            String onStrValue = "";

            if (null != current) {
                currentStrValue = current.toString();
            }

            if (null != onObj) {
                onStrValue = onObj.toString();
            }

            if (onStrValue.equals(dep.where()))  {
                if (null != dep.values() && !ArrayUtils.contains(dep.values(), currentStrValue)) {
                    dv = makeViolation(dep, bean, field, dep.message());
                }
            }
            return dv;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected DataViolation makeViolation(Dependency dep, Object bean, Field field, String message) {
        return new DataFieldViolation("dependency", bean, field.getName(), null == message ? "error.datafield.dependency" : message, dep);
    }
}
