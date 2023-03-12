package com.eagletsoft.framework.plugin.dataview.crud.service;


import com.eagletsoft.boot.framework.data.filter.PageResult;
import com.eagletsoft.boot.framework.data.filter.PageSearch;

import java.io.Serializable;
import java.util.Collection;

public interface ViewService<T> {
    T create(Object bo);
    T update(Serializable id, Object bo);
    T find(Serializable id);
    T get(Serializable id);
    T save(T t);
    void delete(T entity);
    void deleteById(Serializable id);
    void deleteAll(Collection ids);
    PageResult<T> search(PageSearch ps);
    Collection<T> findAll();
}
