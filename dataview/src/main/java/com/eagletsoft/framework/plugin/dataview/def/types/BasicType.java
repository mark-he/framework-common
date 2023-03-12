package com.eagletsoft.framework.plugin.dataview.def.types;

public abstract class BasicType<T> implements IType<T> {
    protected String name;
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public BasicType(String name) {
        this.name = name;
    }
}
