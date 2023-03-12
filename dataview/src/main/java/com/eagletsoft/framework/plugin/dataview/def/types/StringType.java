package com.eagletsoft.framework.plugin.dataview.def.types;

import com.eagletsoft.framework.plugin.dataview.validator.impl.ValidationContext;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class StringType extends BasicType<String> {
    private Integer min = 0;
    private Integer max = Integer.MAX_VALUE;
    private String pattern;
    private String encoding = "UTF-8";
    private Class option;
    private Collection<Object> dict;
    private IDictProvider dictProvider;

    public StringType(String name) {
        super(name);
    }

    public static StringType newDict(String name, List<Object> dict) {
        StringType st = new StringType(name);
        st.setDict(dict);
        return st;
    }

    public static StringType newDict(String name, IDictProvider dictProvider) {
        StringType st = new StringType(name);
        st.dictProvider = dictProvider;
        return st;
    }


    public StringType(String name, String pattern) {
        super(name);
        this.pattern = pattern;
    }

    public StringType(String name, Class option) {
        super(name);
        this.option = option;
    }

    public StringType(String name, int min, int max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(int min) {
        if (min >= 0) {
            this.min = min;
        }
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Collection<Object> getDict() {
        return dict;
    }

    public void setDict(Collection<Object> dict) {
        this.dict = dict;
    }

    @Override
    public Object format(String value) {
        return null != value ? value.trim() : value;
    }

    @Override
    public DataViolation validate(ValidationContext.DataFieldSource source, Object bean, String name, String value) {
        if (null == value) {
            value = "";
        }

        try {
            if (null != this.option) {
                if (!isValidOption(value)) {
                    return new DataViolation(source.root, bean, name, "error.datafield.string.option", value);
                }
            }
            else if (null != this.dict) {
                if (!isValidDict(value, this.dict)) {
                    return new DataViolation(source.root, bean, name, "error.datafield.string.option", value);
                }
            }
            else if (null != this.dictProvider) {
                if (!isValidDict(value, this.dictProvider.getDict().keySet())) {
                    return new DataViolation(source.root, bean, name, "error.datafield.string.option", value);
                }
            }
            else {
                int length = value.length();
                if (null != this.min && length < this.min) {
                    return new DataViolation(source.root, bean, name, "error.datafield.string.min", this.min);
                }

                if (null != this.max && length > this.max) {
                    return new DataViolation(source.root, bean, name, "error.datafield.string.max", this.max);
                }

                if (!StringUtils.isEmpty(this.pattern)) {
                    boolean isMatched = Pattern.matches(this.pattern, value);
                    if (!isMatched) {
                        return new DataViolation(source.root, bean, name, "error.datafield.string.pattern", this.pattern);
                    }
                }
            }
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isValidDict(String str, Collection<Object> dicts) {
        for (Object d : dicts) {
            if (d.toString().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidOption(String str) {
        Field[] fields = this.option.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (field.get("").equals(str)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return false;
    }

    public interface IDictProvider {
        Map<Object, Object> getDict();
    }
}
