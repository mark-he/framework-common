package com.eagletsoft.common.export.excel;

import java.lang.reflect.Field;

public interface IDataRenderer<T> {
    String render(String name, int col, T data, Field property);
}
