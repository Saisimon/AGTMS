package net.saisimon.agtms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.jpa.dialect.Dialect;
import net.saisimon.agtms.jpa.dialect.H2Dialect;
import net.saisimon.agtms.jpa.dialect.MysqlDialect;
import net.saisimon.agtms.jpa.dialect.OracleDialect;
import net.saisimon.agtms.jpa.dialect.PostgresqlDialect;
import net.saisimon.agtms.jpa.dialect.SqlServerDialect;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepositoryFactoryBean;

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
@EnableTransactionManagement
@Slf4j
public class JpaConfig {
	
	@Bean
	@ConditionalOnClass(name= {"com.mysql.jdbc.Driver"})
	public Dialect mysqlDdlService() {
		if (log.isDebugEnabled()) {
			log.debug("User Mysql Dialect");
		}
		return new MysqlDialect();
	}
	
	@Bean
	@ConditionalOnClass(name= {"com.microsoft.sqlserver.jdbc.SQLServerDriver"})
	public Dialect sqlServerDdlService() {
		if (log.isDebugEnabled()) {
			log.debug("User SqlServer Dialect");
		}
		return new SqlServerDialect();
	}
	
	@Bean
	@ConditionalOnClass(name= {"oracle.jdbc.OracleDriver"})
	public Dialect oracleDialect() {
		if (log.isDebugEnabled()) {
			log.debug("User Oracle Dialect");
		}
		return new OracleDialect();
	}
	
	@Bean
	@ConditionalOnClass(name= {"org.postgresql.Driver"})
	public Dialect postgresqlDialect() {
		if (log.isDebugEnabled()) {
			log.debug("User Postgresql Dialect");
		}
		return new PostgresqlDialect();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Dialect h2DdlService() {
		if (log.isDebugEnabled()) {
			log.debug("User H2 Dialect");
		}
		return new H2Dialect();
	}
	
}
