package net.saisimon.agtms.example.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.saisimon.agtms.jpa.repository.base.BaseJpaRepositoryFactoryBean;

@Configuration
@EnableJpaRepositories(basePackages="net.saisimon.agtms.example.repository", repositoryFactoryBeanClass=BaseJpaRepositoryFactoryBean.class)
@EntityScan(basePackages="net.saisimon.agtms.example.domain")
public class JpaConfig {
	
}
