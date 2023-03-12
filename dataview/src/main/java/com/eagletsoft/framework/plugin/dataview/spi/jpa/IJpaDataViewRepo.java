package com.eagletsoft.framework.plugin.dataview.spi.jpa;

import com.eagletsoft.boot.framework.data.repo.IRepo;
import com.eagletsoft.framework.plugin.dataview.data.IDataViewRepo;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IJpaDataViewRepo<T> extends IRepo<T>, IDataViewRepo<T> {
}
