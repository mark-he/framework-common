package com.eagletsoft.framework.plugin.dataview.validator;

import com.eagletsoft.framework.plugin.dataview.def.meta.DataRule;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

public interface IRuleValidator {
    DataViolation validate(DataRule rule, Object obj);
}
