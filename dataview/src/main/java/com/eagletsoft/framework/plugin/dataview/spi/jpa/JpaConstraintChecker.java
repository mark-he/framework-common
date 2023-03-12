package com.eagletsoft.framework.plugin.dataview.spi.jpa;

import com.eagletsoft.boot.framework.data.constraint.ConstraintChecker;
import com.eagletsoft.boot.framework.data.entity.Entity;
import com.eagletsoft.framework.plugin.dataview.validator.checker.IConstraintChecker;

public class JpaConstraintChecker extends ConstraintChecker implements IConstraintChecker {

    @Override
    public boolean checkUnique(Object bean, String... fields) {
        return super.checkUnique((Entity)bean, fields);
    }
}
