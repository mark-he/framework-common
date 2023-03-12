package com.eagletsoft.framework.plugin.dataview.validator.checker;

public interface IConstraintChecker {
    boolean checkUnique(Object bean, String... fields);
}
