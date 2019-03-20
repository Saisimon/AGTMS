package net.saisimon.agtms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.saisimon.agtms.jpa.repository.base.BaseJpaRepositoryFactoryBean;
import net.saisimon.agtms.jpa.service.ddl.H2DdlService;
import net.saisimon.agtms.jpa.service.ddl.MysqlDdlService;

/**
 * JPA 配置类
 * 
 * @author saisimon
 *
 */
@Configuration
@ConditionalOnClass(BaseJpaRepositoryFactoryBean.class)
@EnableJpaRepositories(basePackages="net.saisimon.agtms.jpa.repository", repositoryFactoryBeanClass=BaseJpaRepositoryFactoryBean.class)
@EntityScan(basePackages="net.saisimon.agtms.core.domain.entity")
public class JpaConfig {
	
	@Bean
	@ConditionalOnMissingBean
	public H2DdlService h2DdlService() {
		return new H2DdlService();
	}
	
	@Primary
	@Bean
	@ConditionalOnClass(name= {"com.mysql.jdbc.Driver"})
	public MysqlDdlService mysqlDdlService() {
		return new MysqlDdlService();
	}
	
}
