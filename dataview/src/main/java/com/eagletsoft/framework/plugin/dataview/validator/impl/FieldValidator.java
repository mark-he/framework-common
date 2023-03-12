package com.eagletsoft.framework.plugin.dataview.validator.impl;

import com.eagletsoft.framework.plugin.dataview.def.types.IType;
import com.eagletsoft.framework.plugin.dataview.def.types.TypeRegister;
import com.eagletsoft.framework.plugin.dataview.validator.IFieldValidator;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataFieldViolation;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;

@Component
public class FieldValidator implements IFieldValidator {
    public FieldValidator() {
    }

    @Override
    public DataViolation validate(ValidationContext.DataFieldSource source, Object obj, Field field) {
        try {
            Object value = PropertyUtils.getProperty(obj, field.getName());

            boolean isBlank = false;
            if (null == value) {
                isBlank = true;
            } else if (value instanceof String) {
                isBlank = StringUtils.isEmpty((String)value);
            } else if (value.getClass().isArray()) {
                isBlank = ArrayUtils.isEmpty(((Object [])value));
            } else if (value instanceof Collection) {
                isBlank =  CollectionUtils.isEmpty((Collection)value);
            }

            if (isBlank) {
                if (source.dataField.required()) {
                    return new DataFieldViolation(source.dataField.value(), source.root, obj, field.getName(), "error.datafield.required");
                }
                else {
                    return null;
                }
            }

            IType type = TypeRegister.getInstance().find(source.dataField.value());
            if (null != type) {
                return type.validate(source, obj, field.getName(), value);
            }
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
}
