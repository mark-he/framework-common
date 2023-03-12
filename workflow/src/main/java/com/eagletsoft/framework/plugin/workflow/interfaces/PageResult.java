package com.eagletsoft.framework.plugin.workflow.interfaces;

import org.activiti.engine.query.Query;

import java.util.List;

public class PageResult {
    private long total;
    private List data;
    private int page;
    private int size;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static PageResult make(Query q, PageReq req) {
        PageResult ret = new PageResult();
        ret.setPage(req.getPage());
        ret.setSize(req.getSize());

        ret.setTotal(q.count());
        ret.setData(q.listPage(req.getPage()*req.getSize(), req.getSize()));
        return ret;
    }
}
