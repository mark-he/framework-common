package com.eagletsoft.framework.plugin.dataview.validator.impl;

import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.boot.framework.data.constraint.meta.Unique;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataField;
import com.eagletsoft.framework.plugin.dataview.validator.IUniqueValidator;
import com.eagletsoft.framework.plugin.dataview.validator.checker.IConstraintChecker;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataFieldViolation;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class DataViewUniqueValidator implements IUniqueValidator {

    @Autowired
    private IConstraintChecker constraintChecker;

    @Override
    public DataViolation validate(Object bean, Field field) {
        DataViolation dv = null;
        DataField dataField = field.getAnnotation(DataField.class);

        if (null != dataField && dataField.unique()) {
            boolean ret = constraintChecker.checkUnique(bean, ArrayUtils.add(dataField.uniqueWith(), field.getName()));
            if (!ret) {
                dv = makeViolation(BeanUtils.getProperty(bean, field.getName()), bean, field, null);
            }
        }

        if (null == dv) {
            Unique unique = field.getAnnotation(Unique.class);
            if (null != unique) {
                boolean ret = constraintChecker.checkUnique(bean, ArrayUtils.add(unique.with(), field.getName()));
                if (!ret) {
                    dv = makeViolation(unique, bean, field, unique.message());
                }
            }
        }
        return dv;
    }

    protected DataViolation makeViolation(Object value, Object bean, Field field, String message) {
        return new DataFieldViolation("unique", bean, field.getName(), null == message?"error.datafield.unique" : message, null == value ? "" : value.toString());
    }
}
