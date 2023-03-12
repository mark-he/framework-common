package com.eagletsoft.common.export.excel.impl;

import com.eagletsoft.common.export.excel.IDataMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class GeneralMapper implements IDataMapper {

    @Override
    public Object map(Field property, String data) {
        boolean isEmpty = StringUtils.isEmpty(data);
        switch (property.getType().getSimpleName()) {
            case "String": {
                return data;
            }
            case "Integer": {
                return isEmpty ? 0: Integer.valueOf(data);
            }
            case "Double": {
                return isEmpty ? 0d : Double.valueOf(data);
            }
            case "BigDecimal": {
                return isEmpty ? BigDecimal.ZERO : BigDecimal.valueOf(Double.valueOf(data));
            }
            case "Date": {
                return isEmpty ? null : DateUtil.getJavaDate(Double.valueOf(data));
            }
            default:
                break;
        }
        return data;
    }
}
