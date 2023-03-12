package com.eagletsoft.framework.plugin.dataview.validator.violation;


public class DataFieldViolation extends DataViolation {
    private String type;

    public DataFieldViolation(String type, Class rootClass, Object root, String path, String message, Object... params) {
        super(rootClass, root, path, message, params);
        this.type = type;
    }

    public DataFieldViolation(String type, Object root, String path, String message, Object... params) {
        super(root, path, message, params);
        this.type = type;
    }
}
