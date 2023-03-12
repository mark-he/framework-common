package com.eagletsoft.framework.plugin.dataview.validator.violation;


import java.util.Set;

public class DataValidationException extends RuntimeException {
    private Set<DataViolation> dataViolations;
    public DataValidationException(Set<DataViolation> dataViolations) {
        super("Data Violation Errors");
        this.dataViolations = dataViolations;
    }

    public Set<DataViolation> getDataViolations() {
        return dataViolations;
    }

    public void setDataViolations(Set<DataViolation> dataViolations) {
        this.dataViolations = dataViolations;
    }
}
