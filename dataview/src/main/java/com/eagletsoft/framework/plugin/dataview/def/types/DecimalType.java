package com.eagletsoft.framework.plugin.dataview.def.types;

import com.eagletsoft.framework.plugin.dataview.validator.impl.ValidationContext;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

import java.math.BigDecimal;

public class DecimalType extends BasicType<BigDecimal>  {
    private Long min = Long.MIN_VALUE;
    private Long max = Long.MAX_VALUE;

    private int scale = 2;
    public DecimalType(String name) {
        super(name);
    }

    public DecimalType(String name, long min, long max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
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
    public Object format(BigDecimal value) {
        if (null == value) {
            value = BigDecimal.ZERO;
        }
        return value.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public DataViolation validate(ValidationContext.DataFieldSource source, Object bean, String name, BigDecimal value) {

        if (null == value) {
            value = BigDecimal.ZERO;
        }

        if (null != min && value.compareTo(BigDecimal.valueOf(min)) < 0) {
            return new DataViolation(source.root, bean, name, "error.datafield.decimal.min", min);
        }

        if (null != max && value.compareTo(BigDecimal.valueOf(max)) > 0) {
            return new DataViolation(source.root, bean, name, "error.datafield.decimal.max", max);
        }
        return null;
    }
}
