package com.eagletsoft.framework.plugin.dataview.spi.api;

import com.eagletsoft.boot.framework.api.utils.ApiExceptionResolver;
import com.eagletsoft.boot.framework.api.utils.ApiResponse;
import com.eagletsoft.boot.framework.common.errors.StandardErrors;
import com.eagletsoft.boot.framework.common.i18n.MessageMaker;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataValidationException;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.springframework.beans.factory.annotation.Autowired;

public class DataViewApiExceptionResolver extends ApiExceptionResolver {

    @Autowired
    private MessageMaker messageMaker;

    @Override
    protected ApiResponse otherError(Exception ex) {
        DataValidationException dve = (DataValidationException)this.findCause(ex, DataValidationException.class);

        if (null != dve) {
            String errorMessages = "";
            for (DataViolation dv : dve.getDataViolations()) {
                if (errorMessages.length() > 0) {
                    errorMessages += "; ";
                }
                String namespace = dv.getRootClass().getSimpleName();
                String path = dv.getPath();

                String pathName = messageMaker.makeWithNamespace(namespace, path);

                Object[] params;
                if (null != dv.getParams()) {
                    params = new Object[dv.getParams().length + 1];
                    params[0] = pathName;
                    for (int i = 0; i < dv.getParams().length; i++) {
                        params[i + 1] = dv.getParams()[i];
                    }
                } else {
                    params = new Object[1];
                    params[0] = pathName;
                }

                errorMessages += messageMaker.makeWithNamespace(namespace + "." + path, dv.getMessage(), params);
            }
            return ApiResponse.make(StandardErrors.VALIDATION.getStatus(), errorMessages);
        }
        else {
            return super.otherError(ex);
        }
    }
}
