package net.saisimon.agtms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepositoryFactoryBean;

@Configuration
@ConditionalOnClass(BaseMongodbRepositoryFactoryBean.class)
@EnableMongoRepositories(basePackages="net.saisimon.agtms.mongodb.repository", repositoryFactoryBeanClass=BaseMongodbRepositoryFactoryBean.class)
public class MongodbConfig {
	
}
