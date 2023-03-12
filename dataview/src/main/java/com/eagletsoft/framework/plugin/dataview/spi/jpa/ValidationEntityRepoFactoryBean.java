package com.eagletsoft.framework.plugin.dataview.spi.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class ValidationEntityRepoFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {
    public ValidationEntityRepoFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new ValidationEntityRepoFactoryBean.EntityRepositoryFactory(em);
    }

    private static class EntityRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {
        private final EntityManager em;

        public EntityRepositoryFactory(EntityManager em) {
            super(em);
            this.em = em;
        }

        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            Class clazz = information.getDomainType();
            JpaRepositoryImplementation<?, ?> ret = super.getTargetRepository(information, entityManager);
            ((ValidationEntityDao)ret).setDomainClass(clazz);
            return ret;
        }

        protected Object getTargetRepository(RepositoryMetadata metadata) {
            ValidationEntityDao dao = new ValidationEntityDao(metadata.getDomainType(), this.em);
            return dao;
        }

        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return ValidationEntityDao.class;
        }
    }
}