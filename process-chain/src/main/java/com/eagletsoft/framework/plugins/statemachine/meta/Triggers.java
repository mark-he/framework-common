package com.eagletsoft.framework.plugins.statemachine.meta;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Triggers {
    Trigger[] value();
}