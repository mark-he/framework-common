package com.eagletsoft.framework.plugin.dataview.data.impl;

import com.eagletsoft.boot.framework.common.utils.ApplicationUtils;
import com.eagletsoft.boot.framework.event.Event;
import com.eagletsoft.boot.framework.event.IEventDispatcher;
import com.eagletsoft.framework.plugin.dataview.data.IEntityListener;

import java.io.Serializable;

public class DefaultEntityListener<T> implements IEntityListener<T> {

    @Override
    public void dispatchEvent(String code, String source, Serializable t) {
        IEventDispatcher eventDispatcher = ApplicationUtils.getBean(IEventDispatcher.class);

        if (null != eventDispatcher) {
            eventDispatcher.dispatch(new Event(code, source, t));
        }
    }

    @Override
    public void onCreated(String code, T... t) {
        this.dispatchEvent(code, EntityEventSources.CREATED, t);
    }

    @Override
    public void onUpdated(String code, T... t) {
        this.dispatchEvent(code, EntityEventSources.UPDATED, t);
    }

    @Override
    public void onDeleted(String code, T... t) {
        this.dispatchEvent(code, EntityEventSources.DELETED, t);
    }
}
