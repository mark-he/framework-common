package com.eagletsoft.common.export.excel.meta;

import com.eagletsoft.common.export.excel.impl.GeneralMapper;
import com.eagletsoft.common.export.excel.impl.GeneralRenderer;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ TYPE })
@Retention(RUNTIME)
public @interface XlsModel {
    int startRow() default 1;
    int maxRow() default 65535;
    Class inputMapper() default GeneralMapper.class;
    Class outputRenderer() default GeneralRenderer.class;
}
