package net.saisimon.agtms.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.saisimon.agtms.controller.AgtmsController;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.scanner.TemplateScanner;

/**
 * 模板自动配置类
 * 
 * @author saisimon
 *
 */
@Configuration
@ConditionalOnClass(BaseRepository.class)
public class TemplateAutoConfiguration {
	
	private final ApplicationContext applicationContext;
	
	TemplateAutoConfiguration(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TemplateScanner templateScanner() {
		TemplateScanner templateScanner = new TemplateScanner();
		templateScanner.scan(applicationContext);
		return templateScanner;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AgtmsController agtmsController() {
		return new AgtmsController();
	}
	
}
