package com.eagletsoft.framework.plugin.dataview.validator.impl;

import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataField;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataRule;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataView;
import com.eagletsoft.framework.plugin.dataview.def.meta.Dependency;
import com.eagletsoft.framework.plugin.dataview.utils.DataViewUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationContext {
    private DataRule[] viewDataRules;
    private Map<String, DataField> allFields = new HashMap<>();
    private Map<String, DataFieldSource> viewFields = new HashMap<>();
    private Map<String, DataFieldSource> formFields = new HashMap<>();
    private Map<String, Dependency[]> viewDepends = new HashMap<>();
    private Map<String, Dependency[]> formDepends = new HashMap<>();

    public ValidationContext(Class clazz) {
        super();
        this.viewDataRules = DataViewUtils.findDataRules(clazz);

        DataView dataView = AnnotationUtils.findAnnotation(clazz, DataView.class);
        if (null != dataView && Void.class != dataView.value()) {
            List<Field> fields = BeanUtils.findAllDeclaredFields(dataView.value());
            for (Field f: fields) {
                DataField df = DataViewUtils.findDataField(f);
                if (null != df) {
                    DataFieldSource s = new DataFieldSource(dataView.value(), df);
                    this.formFields.put(f.getName(), s);
                    this.allFields.put(f.getName(), df);
                }
                this.formDepends.put(f.getName(), DataViewUtils.findDependencis(f));
            }
        }

        {
            List<Field> fields = BeanUtils.findAllDeclaredFields(clazz);
            for (Field f : fields) {
                DataField df = DataViewUtils.findDataField(f);
                if (null != df) {
                    DataFieldSource s = new DataFieldSource(clazz, df);
                    this.viewFields.put(f.getName(), s);
                    this.allFields.put(f.getName(), df);
                }
                this.viewDepends.put(f.getName(), DataViewUtils.findDependencis(f));
            }
        }
    }

    public static class DataFieldSource {
        public Class root;
        public DataField dataField;

        public DataFieldSource(Class root, DataField dataField) {
            this.root = root;
            this.dataField = dataField;
        }
    }

    public DataFieldSource findDataField(String fieldName) {
        DataFieldSource df = viewFields.get(fieldName);
        if (null == df) {
            df = formFields.get(fieldName);
        }
        return df;
    }

    public Dependency[] findDataDeps(String fieldName) {
        Dependency[] df = viewDepends.get(fieldName);
        if (null == df || df.length == 0) {
            df = formDepends.get(fieldName);
        }
        return df;
    }

    public DataRule[] findRules() {
        return viewDataRules;
    }

    public Map<String, DataField> findAllDataFields() {
        return allFields;
    }
}
