package com.eagletsoft.framework.plugin.dataview.validator.impl;


import com.eagletsoft.boot.framework.common.errors.ServiceException;
import com.eagletsoft.boot.framework.common.errors.StandardErrors;
import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataRule;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataView;
import com.eagletsoft.framework.plugin.dataview.def.meta.Dependency;
import com.eagletsoft.framework.plugin.dataview.utils.DataViewUtils;
import com.eagletsoft.framework.plugin.dataview.validator.*;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataViewValidator implements IDataViewValidator {

    @Autowired
    private IUniqueValidator uniqueValidator;
    @Autowired
    private IRuleValidator ruleValidator;
    @Autowired
    private IFieldValidator fieldValidator;
    @Autowired
    private IDependencyValidator dependencyValidator;

    protected DataViewValidator() {
    }

    //Validate object for @View
    @Override
    public <T> Set<DataViolation> validate(T var, boolean checkUnique, Class<?>... var2)  {
        return validateObject(var, checkUnique);
    }

    private Set<DataViolation> validateObject(Object bean, boolean checkUnique) {
        DataView v = bean.getClass().getAnnotation(DataView.class);
        if (null == v) {
            return null;
        }

        Set<DataViolation> ret = new HashSet<>();
        ValidationContext context = DataViewUtils.loadContext(bean.getClass());

        if (null != context) {
            try {
                List<Field> fields = BeanUtils.findAllDeclaredFields(bean.getClass());
                for (Field field : fields) {
                    ValidationContext.DataFieldSource dataFieldSource = context.findDataField(field.getName());
                    if (null != dataFieldSource) {
                        DataViolation ret2 = validateDataField(dataFieldSource, bean, field, checkUnique);
                        if (null != ret2) {
                            ret.add(ret2);
                            break;
                        }
                    }

                    Dependency[] deps = context.findDataDeps(field.getName());
                    if (null != deps) {
                        DataViolation ret2 = validateDeps(context.findDataDeps(field.getName()), bean, field);
                        if (null != ret2) {
                            ret.add(ret2);
                            break;
                        }
                    }
                }


                if (null != context.findRules()) {
                    validateRules(context.findRules(), bean);
                }
            }
            catch (ServiceException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new ServiceException(StandardErrors.VALIDATION.getStatus(), ex.getMessage());
            }
        }
        return ret;
    }

    private DataViolation validateDataField(ValidationContext.DataFieldSource dataField, Object bean, Field field, boolean checkUnique) {
        DataViolation ret = fieldValidator.validate(dataField, bean, field);

        if (null == ret && checkUnique) {
            ret = uniqueCheck(bean, field);
        }
        return ret;
    }


    private DataViolation validateDeps(Dependency[] deps, Object bean, Field field) {
        DataViolation ret = null;
        for (Dependency dep : deps) {
            ret = dependencyValidator.validate(dep, bean, field);
            if (null != ret) {
                break;
            }
        }
        return ret;
    }

    private DataViolation validateRules(DataRule[] rules, Object bean) {
        DataViolation ret = null;
        for (DataRule dataRule : rules) {
            ret = ruleValidator.validate(dataRule, bean);
            if (null != ret) {
                break;
            }
        }
        return ret;
    }

    private DataViolation uniqueCheck(Object bean, Field field) {
        if (null != uniqueValidator) {
            return uniqueValidator.validate(bean, field);
        }
        return null;
    }
}
