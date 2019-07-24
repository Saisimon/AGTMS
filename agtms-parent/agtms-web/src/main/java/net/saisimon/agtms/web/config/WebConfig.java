package net.saisimon.agtms.web.config;

import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.web.config.runner.InitRunner;
import net.sf.ehcache.config.ConfigurationFactory;

/**
 * web 配置
 * 
 * @author saisimon
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	/**
	 * 配置国际化
	 * 
	 * @return
	 */
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:i18n/i18n");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(60);
		messageSource.setUseCodeAsDefaultMessage(true);
		return messageSource;
	}
	
	/**
	 * 配置 cookie 解析，默认为简体中文
	 * 
	 * @return
	 */
	@Bean
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		localeResolver.setCookieName("language");
		localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
		return localeResolver;
	}
	
	/**
	 * 任务线程池
	 * 
	 * @return 线程池
	 */
	@Bean("taskThreadPool")
	public SchedulingTaskExecutor taskThreadPool() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		int size = 2 * Runtime.getRuntime().availableProcessors() + 1;
		pool.setCorePoolSize(size);
		pool.setMaxPoolSize(size);
		pool.setQueueCapacity(agtmsProperties().getTaskMaxSize());
		pool.setThreadNamePrefix("Task-Pool-");
		pool.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		pool.initialize();
		return pool;
	}
	
	/**
	 * 操作记录线程池
	 * 
	 * @return 线程池
	 */
	@Bean("operationThreadPool")
	public SchedulingTaskExecutor operationThreadPool() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		int size = 2 * Runtime.getRuntime().availableProcessors() + 1;
		pool.setCorePoolSize(size);
		pool.setMaxPoolSize(size);
		pool.setThreadNamePrefix("Operation-Pool-");
		pool.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		pool.initialize();
		return pool;
	}
	
	/**
	 * 缓存管理器，默认使用 EhCache 管理缓存
	 * 
	 * @return 缓存管理器
	 */
	@Bean
	@ConditionalOnMissingBean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(net.sf.ehcache.CacheManager.create(ConfigurationFactory.parseConfiguration()));
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	/**
	 * 初始化操作
	 * 
	 * @return
	 */
	@Bean
	public InitRunner initRunner() {
		return new InitRunner();
	}
	
	@Bean
	public AgtmsProperties agtmsProperties() {
		return new AgtmsProperties();
	}
	
	@Bean
	public DomainGenerater domainGenerater() {
		return new DomainGenerater(agtmsProperties());
	}
	
}
