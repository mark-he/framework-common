package com.eagletsoft.framework.plugin.dataview.spi;

import com.eagletsoft.framework.plugin.dataview.spi.api.DataViewAdvise;
import com.eagletsoft.framework.plugin.dataview.spi.api.DataViewIntegratedValidator;
import com.eagletsoft.framework.plugin.dataview.spi.jpa.JpaConstraintChecker;
import com.eagletsoft.framework.plugin.dataview.validator.checker.IConstraintChecker;
import com.eagletsoft.framework.plugin.dataview.validator.impl.DataViewValidationFactoryBean;
import org.springframework.context.annotation.Bean;

import javax.validation.Validator;

public class IntegrationPatch {

    @Bean
    public Validator validator() {
        return new DataViewIntegratedValidator();
    }

    @Bean
    public IConstraintChecker constraintChecker() {
        return new JpaConstraintChecker();
    }

    @Bean
    public DataViewAdvise dataViewAdvise() {
        return new DataViewAdvise();
    }

    @Bean
    public DataViewValidationFactoryBean validationFactoryBean(Validator validator) {
        DataViewValidationFactoryBean factoryBean = new DataViewValidationFactoryBean();
        factoryBean.setValidator(validator);

        return factoryBean;
    }
}
