package com.eagletsoft.framework.plugin.dataview.data;

import com.eagletsoft.boot.framework.data.filter.PageSearch;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataView;

@DataView
public class DataViewPageSearch<S> extends PageSearch {
    private S data;

    public S getData() {
        return data;
    }

    public void setData(S data) {
        this.data = data;
    }
}
