package com.eagletsoft.framework.plugins.statemachine.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ TYPE })
@Retention(RUNTIME)
@Repeatable(Triggers.class)
public @interface Trigger {
    String value() default "";
    String criteria() default "";
    String message() default "";
    String event() default "";
    String next() default "";
    Class executor() default Void.class;
    String note() default "";
}