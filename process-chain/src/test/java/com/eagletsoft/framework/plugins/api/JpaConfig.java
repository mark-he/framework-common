package com.eagletsoft.framework.plugins.api;

import com.eagletsoft.boot.framework.data.constraint.ConstraintChecker;
import com.eagletsoft.boot.framework.data.entity.audit.UserSessionAuditor;
import com.eagletsoft.boot.framework.data.filter.SimpleQuery;
import com.eagletsoft.boot.framework.data.json.load.impl.JPALoader;
import com.eagletsoft.boot.framework.data.repo.impl.EntityRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.Serializable;

@Configuration
@EnableJpaRepositories(basePackages = { "com.eagletsoft" }, 
repositoryFactoryBeanClass = EntityRepositoryFactoryBean.class)
@EntityScan(basePackages = { "com.eagletsoft" })
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

	@Bean
	public AuditorAware<Serializable> auditorAware() {
		return new UserSessionAuditor();
	}

	@Bean
	public JPALoader jpaLoader() {
		return new JPALoader();
	}

	@Bean
	public ConstraintChecker constraintChecker() {
		return new ConstraintChecker();
	}

	@Bean
	public SimpleQuery simpleQuery() {
		return new SimpleQuery();
	}
}
