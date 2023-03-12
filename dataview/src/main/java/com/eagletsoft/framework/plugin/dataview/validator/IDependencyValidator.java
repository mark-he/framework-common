package com.eagletsoft.framework.plugin.dataview.validator;

import com.eagletsoft.framework.plugin.dataview.def.meta.Dependency;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

import java.lang.reflect.Field;

public interface IDependencyValidator {
    DataViolation validate(Dependency dep, Object bean, Field field);
}
