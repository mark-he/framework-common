package com.eagletsoft.framework.plugin.dataview.def.types;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TypeRegister {

    private final static TypeRegister INSTANCE = new TypeRegister();
    private Map<String, IType> typeMap = new HashMap<>();

    private TypeRegister() {

    }

    public static TypeRegister getInstance() {
        return INSTANCE;
    }

    public void add(IType type) {
        typeMap.put(type.getName(), type);
    }

    public Map<String, IType> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<String, IType> typeMap) {
        this.typeMap = typeMap;
    }

    public IType find(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        IType type = typeMap.get(name);

        if (null == type) {
            throw new RuntimeException("Type not found: " + name);
        }
        return type;
    }
}
