package com.eagletsoft.framework.plugin.dataview.validator.violation;

public class DataRuleViolation extends DataViolation {
    private String rule;

    public DataRuleViolation(String rule, Object root, String path, String message, Object... params) {
        super(root, path, message, params);
        this.rule = rule;
    }
}
