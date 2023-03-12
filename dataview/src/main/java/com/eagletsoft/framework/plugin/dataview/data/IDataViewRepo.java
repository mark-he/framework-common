package com.eagletsoft.framework.plugin.dataview.data;

import com.eagletsoft.boot.framework.data.filter.PageResult;
import com.eagletsoft.boot.framework.data.filter.PageSearch;

import java.io.Serializable;
import java.util.List;

public interface IDataViewRepo<T> {
    <S extends T> S save(S entity);

    T findOne(Serializable id);

    T getOne(Serializable id);

    List<T> findAll();

    void deleteById(Serializable id);

    void delete(T entity);

    void deleteInBatch(Iterable<T> var1);

    void deleteAllInBatch();

    PageResult<T> search(PageSearch search);

    T newEntity();
}
