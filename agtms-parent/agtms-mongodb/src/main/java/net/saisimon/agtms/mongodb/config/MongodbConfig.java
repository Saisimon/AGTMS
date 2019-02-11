package net.saisimon.agtms.mongodb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import net.saisimon.agtms.mongodb.repository.base.BaseMongoRepositoryFactoryBean;

@Configuration
@EnableMongoRepositories(basePackages="net.saisimon.**.mongodb.repository", repositoryFactoryBeanClass=BaseMongoRepositoryFactoryBean.class)
public class MongodbConfig {
	
}
