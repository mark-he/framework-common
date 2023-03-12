package com.eagletsoft.framework.plugin.dataview.data;

public interface IEntityInterceptor<T> {

    default void onCreate(T entity) {}
    default void onUpdate(T entity) {}
    default void onDelete(T entity) {}
    default void onRead(T entity) {}
}
