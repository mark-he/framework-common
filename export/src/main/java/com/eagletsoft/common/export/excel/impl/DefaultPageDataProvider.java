package com.eagletsoft.common.export.excel.impl;

import com.eagletsoft.boot.framework.common.utils.ApplicationUtils;
import com.eagletsoft.boot.framework.common.utils.PropertyCopyUtil;
import com.eagletsoft.boot.framework.data.filter.PageResult;
import com.eagletsoft.boot.framework.data.filter.PageSearch;
import com.eagletsoft.boot.framework.data.repo.IRepo;
import com.eagletsoft.common.export.excel.IPageDataProvider;

import java.util.ArrayList;
import java.util.List;


public class DefaultPageDataProvider implements IPageDataProvider {
    private boolean end;
    private int page;
    private int size = 500;
    private PageSearch pageSearch;
    private Class entityClass;
    private Class modelClass;
    private IPageQuery pageQuery;

    public DefaultPageDataProvider(PageSearch pageSearch, Class entityClass, Class modelClass) {
        this.pageSearch = pageSearch;
        this.entityClass = entityClass;
        this.modelClass = modelClass;
        pageQuery = new IPageQuery() {
            @Override
            public PageResult search(PageSearch pageSearch) {
                IRepo repo = ApplicationUtils.getBean(IRepo.class, entityClass);
                return repo.search(pageSearch);
            }
        };
    }

    public DefaultPageDataProvider(PageSearch pageSearch, Class entityClass, Class modelClass, IPageQuery pageQuery) {
        this.pageSearch = pageSearch;
        this.entityClass = entityClass;
        this.modelClass = modelClass;
        this.pageQuery = pageQuery;
    }


    public PageSearch getPageSearch() {
        return pageSearch;
    }

    public void setPageSearch(PageSearch pageSearch) {
        this.pageSearch = pageSearch;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }


    public Class getModelClass() {
        return modelClass;
    }

    public void setModelClass(Class modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public List next() {
        List list = new ArrayList();
        if (!end) {
            pageSearch.setPage(page++);
            pageSearch.setSize(size);
            PageResult result = pageQuery.search(pageSearch);

            try {
                if (modelClass.getName().equals(entityClass.getName())) {
                    list = result.getRows();
                }
                for (Object obj : result.getRows()) {
                    Object view = modelClass.newInstance();
                    PropertyCopyUtil.getInstance().copyProperties(view, obj);
                    list.add(view);
                }

                if (result.getTotal() <= (result.getPage() + 1) * result.getSize()) {
                    end = true;
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return list;
    }

    public interface IPageQuery {
        PageResult search(PageSearch pageSearch);
    }
}
