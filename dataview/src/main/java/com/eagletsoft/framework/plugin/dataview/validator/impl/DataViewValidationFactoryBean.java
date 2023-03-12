package com.eagletsoft.framework.plugin.dataview.validator.impl;

import com.eagletsoft.boot.framework.common.validation.JSValidationFactoryBean;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataField;
import com.eagletsoft.framework.plugin.dataview.def.types.DecimalType;
import com.eagletsoft.framework.plugin.dataview.def.types.IType;
import com.eagletsoft.framework.plugin.dataview.def.types.StringType;
import com.eagletsoft.framework.plugin.dataview.def.types.TypeRegister;
import com.eagletsoft.framework.plugin.dataview.utils.DataViewUtils;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataViewValidationFactoryBean extends JSValidationFactoryBean {

    private Validator validator;

    @Override
    public List<Constraint> findConstraints(Class clazz) {
        List<Constraint> constraints = (List<Constraint>)super.cache.get(clazz.getName());

        if (null == constraints) {
            constraints = new ArrayList<>();
            constraints.addAll(this.loadConstraints(clazz));
            constraints.addAll(this.loadDataViewConstraint(clazz));

            super.cache.put(clazz.getName(), constraints);
        }
        return constraints;
    }

    protected List<Constraint> loadDataViewConstraint(Class clazz) {
        List<Constraint> constraints = new ArrayList<>();
        ValidationContext context = DataViewUtils.loadContext(clazz);
        Map<String, DataField> allFields = context.findAllDataFields();

        for (Map.Entry<String, DataField> entry : allFields.entrySet()) {
            DataField df = entry.getValue();
            if (df.required()) {
                Constraint c = new Constraint();
                c.setMessage("error.datafield.required");
                c.setName(entry.getKey());
                c.setType("NotNull");
                constraints.add(c);
            }

            IType type = TypeRegister.getInstance().find(entry.getValue().value());
            if (type instanceof StringType) {
                Constraint c = new Constraint();
                c.setMessage("");
                c.setName(entry.getKey());
                c.setType("Length");
                Map attr = new HashMap();
                attr.put("Min", ((StringType) type).getMin());
                attr.put("Max", ((StringType) type).getMax());
                c.setAtts(attr);
                constraints.add(c);
            } else if (type instanceof DecimalType) {
                Constraint c = new Constraint();
                c.setMessage("");
                c.setName(entry.getKey());
                c.setType("Min");
                Map attr = new HashMap();
                attr.put("Min", ((DecimalType) type).getMin());
                c.setAtts(attr);
                constraints.add(c);

                c = new Constraint();
                c.setMessage("");
                c.setName(entry.getKey());
                c.setType("Max");
                attr = new HashMap();
                attr.put("Max", ((DecimalType) type).getMax());
                c.setAtts(attr);
                constraints.add(c);
            }
        }
        return constraints;
    }

    @Override
    public Validator getValidator() {
        return this.validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}
