package com.eagletsoft.framework.plugin.dataview.def.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ FIELD })
@Retention(RUNTIME)
public @interface DataField {
    String value() default "";
    boolean required() default false;
    String onCreated() default "";
    String onUpdated() default "";
    boolean index() default false;
    boolean unique() default false;
    String[] uniqueWith() default {};
}