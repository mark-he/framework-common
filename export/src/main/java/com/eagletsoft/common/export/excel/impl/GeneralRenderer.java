package com.eagletsoft.common.export.excel.impl;

import com.eagletsoft.common.export.excel.IDataRenderer;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralRenderer<T> implements IDataRenderer<T> {

    @Override
    public String render(String name, int col, T data, Field property) {
        try {
            Object value = PropertyUtils.getProperty(data, property.getName());
            Class type = property.getType();

            boolean isEmpty = null == value;
            switch (type.getSimpleName()) {
                case "BigDecimal": {
                    return isEmpty ? "0" : ((BigDecimal)value).toPlainString();
                }
                case "Date": {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return isEmpty ? "" : sdf.format((Date)value);
                }
                default:
                    return isEmpty ? "" : value.toString();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
