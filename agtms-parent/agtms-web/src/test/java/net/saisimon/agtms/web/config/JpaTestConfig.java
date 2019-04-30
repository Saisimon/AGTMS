package net.saisimon.agtms.web.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.saisimon.agtms.jpa.repository.base.BaseJpaRepositoryFactoryBean;

@TestConfiguration
@EnableJpaRepositories(basePackages="net.saisimon.agtms.web.repository", repositoryFactoryBeanClass=BaseJpaRepositoryFactoryBean.class)
@EntityScan(basePackages="net.saisimon.agtms.web.domain")
public class JpaTestConfig {
	
}
