package com.eagletsoft.framework.plugin.dataview.def.meta;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Dependencies {
	Dependency[] value();
}
