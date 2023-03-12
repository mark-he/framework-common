package com.eagletsoft.framework.plugin.dataview.spi.jpa;

import com.eagletsoft.boot.framework.common.utils.ApplicationUtils;
import com.eagletsoft.boot.framework.data.entity.SoftDelete;
import com.eagletsoft.boot.framework.data.filter.*;
import com.eagletsoft.boot.framework.data.filter.impl.DefaultIndexFinder;
import com.eagletsoft.boot.framework.data.repo.impl.BaseEntityDaoImpl;
import com.eagletsoft.framework.plugin.dataview.data.DataViewIndexFinder;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataView;
import com.eagletsoft.framework.plugin.dataview.utils.DataFieldUtils;
import com.eagletsoft.framework.plugin.dataview.validator.IDataViewValidator;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataValidationException;
import com.eagletsoft.framework.plugin.dataview.validator.violation.DataViolation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

public class ValidationEntityDao<T> extends BaseEntityDaoImpl<T> {

    public ValidationEntityDao(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public ValidationEntityDao(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public <S extends T> S save(S entity) {
        this.validateAndModify(entity);
        return super.save(entity);
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        this.validateAndModify(entity);
        return super.saveAndFlush(entity);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        for (S entity: entities) {
            this.validateAndModify(entity);
        }
        return super.saveAll(entities);
    }

    @Override
    public PageResult<T> search(PageSearch search) {
        return this.search(search, new Criteria.CriteriaModifier[0]);
    }

    @Override
    public PageResult<T> search(PageSearch search, Criteria.CriteriaModifier... cm) {
        PageRequest req = new PageRequest(search.getPage(), search.getSize());
        if (SoftDelete.class.isAssignableFrom(this.getDomainClass())) {
            search.getFilters().add(new ValueFilter("deleted", "=", 0L));
        }

        IndexFinder indexFinder = new DataViewIndexFinder(new DefaultIndexFinder());
        Page<T> page = this.findAll(new Criteria(search, indexFinder, cm), req);
        return PageResult.from(page);
    }


    protected <S extends T> void validateAndModify(S entity) {
        DataView dv = entity.getClass().getAnnotation(DataView.class);
        if (null == dv) {
            return;
        }
        IDataViewValidator dataViewValidator = ApplicationUtils.getBean(IDataViewValidator.class);
        Set<DataViolation> set = dataViewValidator.validate(entity, true);

        if (null != set && !set.isEmpty()) {
            throw new DataValidationException(set);
        }
        DataFieldUtils.normalizeBean(entity);
    }
}
