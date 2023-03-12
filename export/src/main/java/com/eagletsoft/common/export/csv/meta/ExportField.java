package com.eagletsoft.common.export.csv.meta;

import java.lang.annotation.*;

/**
 * 用于绑定实体属性、Excel列名、Excel列索引之间的关系
 * @author allan
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExportField {
    String name() ;//列名
    int position();//列标
}
