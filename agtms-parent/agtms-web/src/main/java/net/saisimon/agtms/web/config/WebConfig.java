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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.property.AccountProperties;
import net.saisimon.agtms.core.property.BasicProperties;
import net.saisimon.agtms.core.property.EncryptorProperties;
import net.saisimon.agtms.core.property.OssProperties;
import net.saisimon.agtms.web.config.interceptor.ResourceInterceptor;
import net.sf.ehcache.config.ConfigurationFactory;

/**
 * Web 配置
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
		pool.setQueueCapacity(basicProperties().getTaskMaxSize());
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
	
	/**
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	/**
	 * AGTMS 基础属性对象
	 * 
	 * @return
	 */
	@Bean
	public BasicProperties basicProperties() {
		return new BasicProperties();
	}
	
	/**
	 * AGTMS 账户属性对象
	 * 
	 * @return
	 */
	@Bean
	public AccountProperties accountProperties() {
		return new AccountProperties();
	}
	
	/**
	 * AGTMS 加解密属性对象
	 * 
	 * @return
	 */
	@Bean
	public EncryptorProperties encryptorProperties() {
		return new EncryptorProperties();
	}
	
	/**
	 * AGTMS OSS 属性对象
	 * 
	 * @return
	 */
	@Bean
	public OssProperties ossProperties() {
		return new OssProperties();
	}
	
	/**
	 * 对象生成器
	 * 
	 * @return
	 */
	@Bean
	public DomainGenerater domainGenerater() {
		return new DomainGenerater(basicProperties());
	}
	
	/**
	 * 资源权限拦截器
	 * 
	 * @return
	 */
	@Bean
	public ResourceInterceptor resourceInterceptor() {
		return new ResourceInterceptor();
	}
	
	/**
	 * 添加拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(resourceInterceptor());
	}
	
}
