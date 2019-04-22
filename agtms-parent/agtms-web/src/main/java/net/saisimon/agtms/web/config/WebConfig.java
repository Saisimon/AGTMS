package net.saisimon.agtms.web.config;

import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import net.saisimon.agtms.web.config.filter.AccessFilter;
import net.saisimon.agtms.web.config.property.WhiteList;
import net.saisimon.agtms.web.config.property.WhitePrefix;
import net.saisimon.agtms.web.config.runner.InitRunner;

/**
 * web 配置
 * 
 * @author saisimon
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	/**
	 * 任务队列大小
	 */
	@Value("${extra.max-size.task:1024}")
	private int taskMaxSize;
	
	@Bean
	@ConfigurationProperties(prefix="white.list")
	public WhiteList whiteList() {
		return new WhiteList();
	}
	
	@Bean
	@ConfigurationProperties(prefix="white.prefix")
	public WhitePrefix whitePrefix() {
		return new WhitePrefix();
	}
	
	/**
	 * 跨域配置
	 */
	@Bean
	public CorsConfiguration corsConfiguration() {
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		return config;
	}
	
	/**
	 * 跨域过滤器
	 */
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration());
		return new CorsFilter(source);
	}
	
	/**
	 * 访问过滤器
	 */
	@Bean
	public FilterRegistrationBean<AccessFilter> accessFilter() {
		FilterRegistrationBean<AccessFilter> registration = new FilterRegistrationBean<>();
		AccessFilter accessFilter = new AccessFilter();
		accessFilter.setWhiteList(whiteList());
		accessFilter.setWhitePrefix(whitePrefix());
		accessFilter.setCorsConfiguration(corsConfiguration());
		registration.setFilter(accessFilter);
		registration.addUrlPatterns("/*");
		registration.setName("accessFilter");
		return registration;
	}
	
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
		pool.setCorePoolSize(2 * Runtime.getRuntime().availableProcessors() + 1);
		pool.setMaxPoolSize(100);
		pool.setQueueCapacity(taskMaxSize);
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
		int size = Runtime.getRuntime().availableProcessors() + 1;
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
		return new EhCacheCacheManager();
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
	
}
