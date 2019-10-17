package net.saisimon.agtms.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.property.BasicProperties;
import net.saisimon.agtms.jpa.dialect.Dialect;
import net.saisimon.agtms.jpa.dialect.H2Dialect;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepositoryFactoryBean;

@TestConfiguration
@EnableJpaRepositories(basePackages="net.saisimon.agtms.jpa.repository", repositoryFactoryBeanClass=BaseJpaRepositoryFactoryBean.class)
@EntityScan(basePackages= {"net.saisimon.agtms.core.domain.entity", "net.saisimon.agtms.jpa.domain"})
public class JpaTestConfig {
	
	@Bean
	public Dialect h2DdlService() {
		return new H2Dialect();
	}
	
	@Bean
	public BasicProperties basicProperties() {
		return new BasicProperties();
	}
	
	@Bean
	public DomainGenerater domainGenerater() {
		return new DomainGenerater(basicProperties());
	}
	
}
