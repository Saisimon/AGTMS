package net.saisimon.agtms.mongodb.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepositoryFactoryBean;

@TestConfiguration
@EnableMongoRepositories(basePackages="net.saisimon.agtms.mongodb.repository", repositoryFactoryBeanClass=BaseMongodbRepositoryFactoryBean.class)
public class MongodbTestConfig {
	
	@Bean
	public AgtmsProperties agtmsProperties() {
		return new AgtmsProperties();
	}
	
	@Bean
	public DomainGenerater domainGenerater() {
		return new DomainGenerater(agtmsProperties());
	}
	
}
