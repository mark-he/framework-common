package com.eagletsoft.framework.plugin.dataview.validator;

import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

import java.util.Set;

public interface IDataViewValidator {
    //Validate object for @View
    <T> Set<DataViolation> validate(T var, boolean checkUnique, Class<?>... var2);
}
