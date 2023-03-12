package com.eagletsoft.framework.plugin.dataview.utils;

import com.eagletsoft.framework.plugin.dataview.def.meta.DataField;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataRule;
import com.eagletsoft.framework.plugin.dataview.def.meta.Dependency;
import com.eagletsoft.framework.plugin.dataview.validator.impl.ValidationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataViewUtils {

    public static DataRule[] findDataRules(Class clazz) {
        Set<DataRule> rules = AnnotationUtils.getRepeatableAnnotations(clazz, DataRule.class);
        return rules.toArray(new DataRule[0]);
    }

    public static Dependency[] findDependencis(Field field) {
        Set<Dependency> deps = AnnotationUtils.getRepeatableAnnotations(field, Dependency.class);
        return deps.toArray(new Dependency[0]);
    }

    public static DataField findDataField(Field field) {
        return AnnotationUtils.findAnnotation(field, DataField.class);
    }

    private static Map<String, ValidationContext> CONTEXT_MAP = new ConcurrentHashMap<>();

    public static ValidationContext loadContext(Class clazz) {
        ValidationContext context = CONTEXT_MAP.get(clazz.getName());
        if (null == context) {
            context = new ValidationContext(clazz);
            CONTEXT_MAP.put(clazz.getName(), context);
        }
        return context;
    }
}
