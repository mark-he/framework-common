package com.eagletsoft.framework.plugins.processchain.meta;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Activities {
    Activity[] value();
}