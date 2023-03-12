package com.eagletsoft.framework.plugin.dataview.validator;

import com.eagletsoft.framework.plugin.dataview.validator.impl.ValidationContext;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

import java.lang.reflect.Field;

public interface IFieldValidator {
    DataViolation validate(ValidationContext.DataFieldSource dataField, Object obj, Field field);
}
