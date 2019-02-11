package net.saisimon.agtms.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.saisimon.agtms.jpa.repository.base.BaseJpaRepositoryFactoryBean;

@Configuration
@EnableJpaRepositories(basePackages="net.saisimon.**.jpa.repository", repositoryFactoryBeanClass=BaseJpaRepositoryFactoryBean.class)
@EntityScan(basePackages="net.saisimon.agtms.core.domain")
public class JpaConfig {
	
}
