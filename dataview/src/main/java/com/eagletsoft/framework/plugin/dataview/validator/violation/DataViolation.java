package com.eagletsoft.framework.plugin.dataview.validator.violation;

public class DataViolation {
    private Class rootClass;
    private Object root;
    private String path;
    private String message;
    private Object[] params;
    public DataViolation() {}

    public DataViolation(Class rootClass, Object root, String path, String message, Object... params) {
        this.rootClass = rootClass;
        this.root = root;
        this.path = path;
        this.message = message;
        this.params = params;
    }

    public DataViolation(Object root, String path, String message, Object... params) {
        this.rootClass = root.getClass();
        this.root = root;
        this.path = path;
        this.message = message;
        this.params = params;
    }

    public Object getRoot() {
        return root;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getParams() {
        return params;
    }

    public Class getRootClass() {
        return rootClass;
    }
}
