package com.eagletsoft.framework.plugin.dataview.spi.jpa;

import com.eagletsoft.boot.framework.data.filter.IndexFinder;
import com.eagletsoft.boot.framework.data.filter.NativeQueryBuilder;
import com.eagletsoft.boot.framework.data.filter.impl.DefaultIndexFinder;
import com.eagletsoft.framework.plugin.dataview.data.DataViewIndexFinder;

public class NativeQueryBuilderFactory {

    public static NativeQueryBuilder create() {
        IndexFinder indexFinder = new DataViewIndexFinder(new DefaultIndexFinder());
        return NativeQueryBuilder.newInstance().setIndexFinder(indexFinder);
    }
}
