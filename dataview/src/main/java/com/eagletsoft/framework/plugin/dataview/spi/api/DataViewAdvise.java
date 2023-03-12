package com.eagletsoft.framework.plugin.dataview.spi.api;

import com.eagletsoft.framework.plugin.dataview.utils.DataFieldUtils;
import com.eagletsoft.framework.plugin.dataview.validator.IDataViewValidator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;

@Aspect
public class DataViewAdvise {
	@Autowired
	private IDataViewValidator IDataViewValidator;

	@Around("(@annotation(ann))")
	public Object aroundMethod(final ProceedingJoinPoint pjd, ResponseBody ann) throws Throwable {

		for (Object arg : pjd.getArgs()) {
			DataFieldUtils.normalizeBean(arg);
		}
		Object result = pjd.proceed();
		DataFieldUtils.normalizeBean(result);
		return result;
	}
}
