package com.eagletsoft.framework.plugin.dataview.def.meta.layout;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ FIELD })
@Retention(RUNTIME)
public @interface LayoutField {
    //label
    String title();
    String field();
    String type();
    //default value
    String value();
    boolean hidden();
    boolean readonly();
    int colspan() default 1;
    int order();
    int section();
    String layout();
    String hint();
}