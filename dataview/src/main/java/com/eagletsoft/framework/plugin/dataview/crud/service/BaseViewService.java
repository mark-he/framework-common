package com.eagletsoft.framework.plugin.dataview.crud.service;

import com.eagletsoft.boot.framework.common.errors.ServiceException;
import com.eagletsoft.boot.framework.common.errors.StandardErrors;
import com.eagletsoft.boot.framework.common.utils.PropertyCopyUtil;
import com.eagletsoft.boot.framework.data.entity.Entity;
import com.eagletsoft.boot.framework.data.filter.PageResult;
import com.eagletsoft.boot.framework.data.filter.PageSearch;
import com.eagletsoft.framework.plugin.dataview.data.IDataViewRepo;
import com.eagletsoft.framework.plugin.dataview.data.IEntityInterceptor;
import com.eagletsoft.framework.plugin.dataview.data.IEntityListener;
import com.eagletsoft.framework.plugin.dataview.data.impl.DefaultEntityInterceptor;
import com.eagletsoft.framework.plugin.dataview.data.impl.DefaultEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collection;

@Transactional(rollbackFor = {Exception.class})
public abstract class BaseViewService<T extends Entity> implements ViewService<T> {

    protected abstract IDataViewRepo<T> getRepo();
    protected IEntityListener<T> listener = new DefaultEntityListener<>();

    public T newEntity() {
        return getRepo().newEntity();
    }

    @Override
    public T create(Object bo) {
        T entity = getRepo().newEntity();
        return this.createWithData(entity, bo);
    }

    public T createWithData(T entity, Object bo) {
        onCreate(entity, bo);
        return createWithEntity(entity, bo);
    }

    protected T createWithEntity(T entity, Object bo) {
        this.save(entity);
        afterCreated(entity, bo);
        return entity;
    }

    protected void onCreate(T entity, Object bo) {
        mergeData(bo, entity);
    }

    protected void afterSaved(T entity, boolean justCreated) {
        if (justCreated) {
            listener.onCreated(entity.getClass().getName(), entity);
        } else {
            listener.onUpdated(entity.getClass().getName(), entity);
        }
    }

    protected void afterCreated(T entity, Object bo) {
    }

    @Override
    public T save(T entity) {
        onSave(entity);
        if (null == entity.getId()) {
            getRepo().save(entity);
            afterSaved(entity, true);

        } else {
            getRepo().save(entity);
            afterSaved(entity, false);
        }
        return entity;
    }

    @Override
    public T update(Serializable id, Object bo) {
        T entity = this.find(id);
        return updateWithData(entity, bo);
    }

    public T updateWithData(T entity, Object bo) {
        onUpdate(entity, bo);
        return this.updateWithEntity(entity, bo);
    }

    protected T updateWithEntity(T entity, Object bo) {
        this.save(entity);
        afterUpdated(entity, bo);
        return entity;
    }

    protected void onUpdate(T entity, Object bo) {
        mergeData(bo, entity);
    }

    protected void onSave(T entity) {};

    protected void afterUpdated(T entity, Object bo) {
    }

    @Override
    public T find(Serializable id) {
        T entity = getRepo().findOne(id);
        return entity;
    }

    @Override
    public T get(Serializable id) {
        T entity = getRepo().getOne(id);
        return entity;
    }

    @Override
    public void deleteById(Serializable id) {
        T entity = this.find(id);
        this.delete(entity);
    }

    @Override
    public void delete(T entity) {
        onDelete(entity);
        getRepo().delete(entity);
        afterDeleted(entity);
    }

    protected void onDelete(T entity) {
    }

    protected void afterDeleted(T entity) {
        listener.onDeleted(entity.getClass().getName(), entity);
    }

    @Override
    public void deleteAll(Collection ids) {
        getRepo().deleteInBatch(ids);
    }

    @Override
    public Collection<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public PageResult<T> search(PageSearch ps) {
        return getRepo().search(ps);
    }

    public void mergeData(Object src, T target) {
        try {
            PropertyCopyUtil.getInstance().copyPropertiesWithIgnores(target, src, "id", "createdTime", "createdBy", "updatedTime", "updatedBy", "deleted");
        }
        catch (Exception ex) {
            throw new ServiceException(StandardErrors.INTERNAL_ERROR.getStatus(), ex.getMessage());
        }
    }
}
