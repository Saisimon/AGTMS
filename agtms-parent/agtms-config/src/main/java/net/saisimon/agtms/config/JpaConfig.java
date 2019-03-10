package net.saisimon.agtms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.saisimon.agtms.jpa.repository.base.BaseJpaRepositoryFactoryBean;
import net.saisimon.agtms.jpa.service.ddl.DdlService;
import net.saisimon.agtms.jpa.service.ddl.DefaultDdlService;

@Configuration
@ConditionalOnClass(BaseJpaRepositoryFactoryBean.class)
@EnableJpaRepositories(basePackages="net.saisimon.agtms.jpa.repository", repositoryFactoryBeanClass=BaseJpaRepositoryFactoryBean.class)
@EntityScan(basePackages="net.saisimon.agtms.core.domain.entity")
public class JpaConfig {
	
	@Bean
	@ConditionalOnMissingBean
	public DdlService ddlService() {
		return new DefaultDdlService();
	}
	
}
