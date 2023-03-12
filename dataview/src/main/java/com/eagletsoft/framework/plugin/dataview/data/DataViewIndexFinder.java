package com.eagletsoft.framework.plugin.dataview.data;

import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.boot.framework.data.filter.IndexFinder;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataField;
import com.eagletsoft.framework.plugin.dataview.utils.DataViewUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class DataViewIndexFinder implements IndexFinder {
    private IndexFinder inner;

    public DataViewIndexFinder(IndexFinder inner) {
        this.inner = inner;
    }

    @Override
    public boolean isIndex(Class root, String fieldName) {
        boolean ret = false;
        try {
            Map<String, Field> map = BeanUtils.findAllDeclaredFieldMap(root);

            Field f = map.get(fieldName);
            if (null != f) {
                DataField df = DataViewUtils.findDataField(f);
                if (null != df && df.index()) {
                    ret = true;
                }
            }
            if (!ret && null != inner) {
                ret = inner.isIndex(root, fieldName);
            }
        } catch (Exception ex) {
            ret = false;
        }
        return ret;
    }
}
