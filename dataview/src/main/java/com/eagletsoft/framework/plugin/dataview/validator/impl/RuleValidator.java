package com.eagletsoft.framework.plugin.dataview.validator.impl;

import com.eagletsoft.boot.framework.common.errors.ServiceException;
import com.eagletsoft.boot.framework.common.errors.StandardErrors;
import com.eagletsoft.boot.framework.common.utils.ExpressionUtils;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataRule;
import com.eagletsoft.framework.plugin.dataview.validator.IRuleValidator;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataRuleViolation;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.springframework.stereotype.Component;

@Component
public class RuleValidator implements IRuleValidator {

    @Override
    public DataViolation validate(DataRule rule, Object obj) {
        Object result = ExpressionUtils.readValue(rule.value(), obj);
        if (null == result || !(result instanceof Boolean)) {
            throw new ServiceException(StandardErrors.VALIDATION.getStatus(), "Invalid rule was defined in " + obj.getClass().getName() + " : " + rule.value());
        }

        if (false == ((Boolean)result).booleanValue()) {
            return new DataRuleViolation(rule.value(), obj, obj.getClass().getName(), rule.message());
        }
        return null;
    }
}
