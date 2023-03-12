package com.eagletsoft.framework.plugin.dataview.def.types;

import com.eagletsoft.framework.plugin.dataview.validator.impl.ValidationContext;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

public interface IType<T> {
    String getName();
    Object format(T value);
    DataViolation validate(ValidationContext.DataFieldSource dataField, Object bean, String name, T value);
}
