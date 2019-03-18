package net.saisimon.agtms.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.saisimon.agtms.jpa.repository.base.BaseJpaRepositoryFactoryBean;
import net.saisimon.agtms.jpa.service.ddl.DdlService;
import net.saisimon.agtms.jpa.service.ddl.DefaultDdlService;

@TestConfiguration
@EnableJpaRepositories(basePackages="net.saisimon.agtms.jpa.repository", repositoryFactoryBeanClass=BaseJpaRepositoryFactoryBean.class)
@EntityScan(basePackages="net.saisimon.agtms.core.domain.entity")
public class JpaTestConfig {
	
	@Bean
	public DdlService ddlService() {
		return new DefaultDdlService();
	}
	
}