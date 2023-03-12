package com.eagletsoft.framework.plugin.dataview.validator;

import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

import java.lang.reflect.Field;

public interface IUniqueValidator {
    DataViolation validate(Object bean, Field field);
}
