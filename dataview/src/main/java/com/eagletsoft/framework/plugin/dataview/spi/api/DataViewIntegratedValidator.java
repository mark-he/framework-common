package com.eagletsoft.framework.plugin.dataview.spi.api;

import com.eagletsoft.framework.plugin.dataview.validator.IDataViewValidator;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataValidationException;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.*;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Set;

public class DataViewIntegratedValidator implements Validator {
    private Validator basicValidator;
    @Autowired
    private IDataViewValidator dataViewValidator;

    public DataViewIntegratedValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
                .configure()
                .addProperty( "hibernate.validator.fail_fast", "true" )
                .buildValidatorFactory();
        basicValidator = validatorFactory.getValidator();
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes) {
        Set<ConstraintViolation<T>> ret = basicValidator.validate(t, classes);
        if (null == ret || ret.isEmpty()) {
            Set<DataViolation> rets = dataViewValidator.validate(t, false, classes);
            if (null != rets && !rets.isEmpty()) {
                throw new DataValidationException(rets);
            }
        } else {
            throw new ConstraintViolationException(ret);
        }
        return ret;
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes) {
        return basicValidator.validateProperty(t, s, classes);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> aClass, String s, Object o, Class<?>... classes) {
        return basicValidator.validateValue(aClass, s, o, classes);
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> aClass) {
        return basicValidator.getConstraintsForClass(aClass);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return basicValidator.unwrap(aClass);
    }

    @Override
    public ExecutableValidator forExecutables() {
        return basicValidator.forExecutables();
    }
}
