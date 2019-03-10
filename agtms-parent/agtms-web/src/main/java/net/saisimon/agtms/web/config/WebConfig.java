package net.saisimon.agtms.web.config;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import net.saisimon.agtms.core.util.PropertyUtils;

/**
 * web 配置
 * 
 * @author saisimon
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	private static final int TASK_MAX_SIZE = NumberUtils.toInt(PropertyUtils.fetchYaml("extra.max-size.task", 3).toString(), 3);
	
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
	
	@Bean
	public ExecutorService executorService() {
		return Executors.newFixedThreadPool(TASK_MAX_SIZE);
	}

}
