package com.eagletsoft.framework.plugins.processchain.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ TYPE })
@Retention(RUNTIME)
@Repeatable(Activities.class)
public @interface Activity {
    String name() default "";
    Class executor() default Void.class;
    String executorMethod() default "";
    String note() default "";
    String[] events() default {};
    String callback() default "";
}