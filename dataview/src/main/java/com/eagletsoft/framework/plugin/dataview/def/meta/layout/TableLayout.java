package com.eagletsoft.framework.plugin.dataview.def.meta.layout;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ TYPE })
@Retention(RUNTIME)
public @interface TableLayout {
    String[] headers();
    String[] keys();
    Class renderClass();
}