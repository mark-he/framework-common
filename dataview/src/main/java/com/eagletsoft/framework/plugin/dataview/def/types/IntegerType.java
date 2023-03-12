package com.eagletsoft.framework.plugin.dataview.def.types;

import com.eagletsoft.framework.plugin.dataview.validator.impl.ValidationContext;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

public class IntegerType extends BasicType<Integer>  {
    private Long min = Long.MIN_VALUE;
    private Long max = Long.MAX_VALUE;

    public IntegerType(String name) {
        super(name);
    }

    public IntegerType(String name, long min, long max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    @Override
    public Object format(Integer value) {
        if (null == value) {
            value = 0;
        }
        return value;
    }

    @Override
    public DataViolation validate(ValidationContext.DataFieldSource source, Object bean, String name, Integer value) {

        if (null == value) {
            value = 0;
        }

        if (null != min && value < min) {
            return new DataViolation(source.root, bean, name, "error.datafield.decimal.min", min);
        }

        if (null != max && value > max) {
            return new DataViolation(source.root, bean, name, "error.datafield.decimal.max", max);
        }
        return null;
    }
}
