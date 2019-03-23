package net.saisimon.agtms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class JpaConfig {
	
	@Primary
	@Bean
	@ConditionalOnClass(name= {"com.mysql.jdbc.Driver"})
	public MysqlDialect mysqlDdlService() {
		if (log.isDebugEnabled()) {
			log.debug("User Mysql Dialect");
		}
		return new MysqlDialect();
	}
	
	@Primary
	@Bean
	@ConditionalOnClass(name= {"com.microsoft.sqlserver.jdbc.SQLServerDriver"})
	public SqlServerDialect sqlServerDdlService() {
		if (log.isDebugEnabled()) {
			log.debug("User SqlServer Dialect");
		}
		return new SqlServerDialect();
	}
	
	@Primary
	@Bean
	@ConditionalOnClass(name= {"oracle.jdbc.OracleDriver"})
	public OracleDialect oracleDialect() {
		if (log.isDebugEnabled()) {
			log.debug("User Oracle Dialect");
		}
		return new OracleDialect();
	}
	
	@Primary
	@Bean
	@ConditionalOnClass(name= {"org.postgresql.Driver"})
	public PostgresqlDialect postgresqlDialect() {
		if (log.isDebugEnabled()) {
			log.debug("User Postgresql Dialect");
		}
		return new PostgresqlDialect();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public H2Dialect h2DdlService() {
		if (log.isDebugEnabled()) {
			log.debug("User H2 Dialect");
		}
		return new H2Dialect();
	}
	
}
