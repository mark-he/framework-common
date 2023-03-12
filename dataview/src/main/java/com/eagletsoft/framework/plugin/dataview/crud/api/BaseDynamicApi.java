package com.eagletsoft.framework.plugin.dataview.crud.api;

import com.eagletsoft.boot.framework.api.utils.ApiResponse;
import com.eagletsoft.boot.framework.common.security.meta.Access;
import com.eagletsoft.framework.plugin.dataview.crud.service.ViewService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.Serializable;

public abstract class BaseDynamicApi<CO1, CO2> {
    protected ViewService service;

    @PostConstruct
    protected abstract void config();

    @Access(":read")
    @PostMapping("/{formId}/{id}/read")
    public @ResponseBody Object read(@PathVariable String formId, @PathVariable Serializable id) {
        return ApiResponse.make(service.find(id));
    }

    @Access(":create")
    @PostMapping("/{formId}/create")
    public @ResponseBody Object create(@PathVariable String formId, @RequestBody @Valid CO1 co) {
        Object bo = convert(co);
        return ApiResponse.make(service.create(co));
    }

    @Access(":update")
    @PostMapping("/{formId}/{id}/update")
    public @ResponseBody Object update(@PathVariable String formId, @PathVariable Serializable id, @RequestBody @Valid CO2 co) {
        Object bo = convert(co);
        return ApiResponse.make(service.update(formId, co));
    }

    @Access(":remove")
    @PostMapping("/{formId}/{id}/remove")
    public @ResponseBody Object remove(@PathVariable String formId, @PathVariable Serializable id) {
        service.deleteById(id);
        return ApiResponse.make();
    }

    protected Object convert(Object co) {
        return co;
    }
}
