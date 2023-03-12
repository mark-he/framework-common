package com.eagletsoft.framework.plugin.dataview.def.meta;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})

public @interface DataRules {
	DataRule[] value();
}
