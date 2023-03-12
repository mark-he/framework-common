package com.eagletsoft.common.export.csv.meta;


import com.eagletsoft.common.export.csv.ViewRender;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ExportViewRender {
	Class<ViewRender> value();
}
