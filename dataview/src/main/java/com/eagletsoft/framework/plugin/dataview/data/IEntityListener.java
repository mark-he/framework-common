package com.eagletsoft.framework.plugin.dataview.data;

import java.io.Serializable;

public interface IEntityListener<T> {
    void onCreated(String code, T... t);
    void onUpdated(String code, T... t);
    void onDeleted(String code, T... t);
    void dispatchEvent(String code, String source, Serializable t);

    interface EntityEventSources {
        String CREATED = "CREATED";
        String UPDATED = "UPDATED";
        String DELETED = "DELETED";
    }
}
