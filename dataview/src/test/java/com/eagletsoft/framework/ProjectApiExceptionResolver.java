package com.eagletsoft.framework;

import com.eagletsoft.framework.plugin.dataview.spi.api.DataViewApiExceptionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProjectApiExceptionResolver extends DataViewApiExceptionResolver {
    private static Logger LOG = LoggerFactory.getLogger(ProjectApiExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ex.printStackTrace();

        CoreFramework.getInstance().rollback();
        ModelAndView ret = super.resolveException(request, response, handler, ex);
        return ret;
    }
}
