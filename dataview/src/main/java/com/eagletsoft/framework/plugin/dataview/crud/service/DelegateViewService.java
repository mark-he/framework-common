package com.eagletsoft.framework.plugin.dataview.crud.service;

import com.eagletsoft.boot.framework.data.filter.PageResult;
import com.eagletsoft.boot.framework.data.filter.PageSearch;

import java.io.Serializable;
import java.util.Collection;

public abstract class DelegateViewService<T> implements ViewService<T> {

    protected abstract ViewService<T> getEntityService();

    @Override
    public T create(Object bo) {
        return getEntityService().create(bo);
    }

    @Override
    public T update(Serializable id, Object bo) {
        return getEntityService().update(id, bo);
    }

    @Override
    public T find(Serializable id) {
        return getEntityService().find(id);
    }

    @Override
    public void delete(T entity) {
        getEntityService().delete(entity);
    }

    @Override
    public void deleteById(Serializable id) {
        getEntityService().deleteById(id);
    }

    @Override
    public PageResult<T> search(PageSearch ps) {
        return getEntityService().search(ps);
    }

    @Override
    public T get(Serializable id) {
        return getEntityService().get(id);
    }

    @Override
    public T save(T t) {
        return getEntityService().save(t);
    }

    @Override
    public void deleteAll(Collection ids) {
        getEntityService().deleteAll(ids);
    }

    @Override
    public Collection<T> findAll() {
        return getEntityService().findAll();
    }
}
