package com.eagletsoft.framework.plugin.dataview.crud.api;

import com.eagletsoft.boot.framework.api.utils.ApiResponse;
import com.eagletsoft.boot.framework.common.security.meta.Access;
import com.eagletsoft.boot.framework.data.filter.PageResult;
import com.eagletsoft.framework.plugin.dataview.crud.service.ViewService;
import com.eagletsoft.framework.plugin.dataview.data.DataViewPageSearch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.Serializable;

public abstract class BaseViewApi<CO1 extends Serializable, CO2 extends Serializable, S extends Object, T> {
    protected abstract ViewService getService();

    @PostConstruct
    protected void init() {};

    @Access(":read")
    @PostMapping("/{id}/read")
    public @ResponseBody ApiResponse<T> read(@PathVariable("id") Serializable id) {
        return ApiResponse.make(getService().find(id));
    }

    @Access(":create")
    @PostMapping("/create")
    public @ResponseBody ApiResponse<T> create(@RequestBody @Valid CO1 co) {
        return ApiResponse.make(getService().create(co));
    }

    @Access(":update")
    @PostMapping("/{id}/update")
    public @ResponseBody ApiResponse<T> update(@PathVariable("id") Serializable id, @RequestBody @Valid CO2 co) { ;
        return ApiResponse.make(getService().update(id, co));
    }

    @Access(":remove")
    @PostMapping("/{id}/remove")
    public @ResponseBody ApiResponse remove(@PathVariable("id") Serializable id) {
        getService().deleteById(id);
        return ApiResponse.make();
    }

    @Access(":read")
    @PostMapping("/search")
    public @ResponseBody ApiResponse<PageResult<T>> search(@RequestBody @Valid DataViewPageSearch<S> ps) {
        return ApiResponse.make(getService().search(ps));
    }
}
