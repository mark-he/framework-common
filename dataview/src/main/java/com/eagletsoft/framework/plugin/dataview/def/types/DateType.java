package com.eagletsoft.framework.plugin.dataview.def.types;

import com.eagletsoft.boot.framework.common.utils.DateUtils;
import com.eagletsoft.framework.plugin.dataview.validator.impl.ValidationContext;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;

import java.util.Date;

public class DateType extends BasicType<Date> {
    private Date min;
    private Date max;
    private String type;

    public DateType(String name) {
        super(name);
    }

    public DateType(String name, String type) {
        super(name);
        this.type = type;
    }

    public DateType(String name, Date min, Date max, String type) {
        super(name);
        this.min = min;
        this.max = max;
        this.type = type;
    }

    public Date getMin() {
        return min;
    }

    public void setMin(Date min) {
        this.min = min;
    }

    public Date getMax() {
        return max;
    }

    public void setMax(Date max) {
        this.max = max;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Object format(Date value) {
        if (null == value) {
            return null;
        }
        if (Types.END.equals(type)) {
            return DateUtils.end(value);
        }
        else if (Types.BEGIN.equals(type)) {
            return DateUtils.start(value);
        }
        return value;
    }

    @Override
    public DataViolation validate(ValidationContext.DataFieldSource source, Object bean, String name, Date value) {
        if (null == value) {
            return null;
        }
        if (null != min && value.compareTo(min) < 0) {
            return new DataViolation(source.root, bean, name, "error.datafield.date.min", min);
        }

        if (null != max && value.compareTo(max) > 0) {
            return new DataViolation(source.root, bean, name, "error.datafield.date.max", max);
        }
        return null;
    }

    public interface Types {
        String BEGIN = "DayBegin";
        String END = "DayEnd";
    }
}
