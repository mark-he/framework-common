package com.eagletsoft.common.export.excel;

import java.lang.reflect.Field;

public interface IDataMapper<T> {
    T map(Field property, String data);
}
