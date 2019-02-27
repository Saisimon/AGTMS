package net.saisimon.agtms.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.saisimon.agtms.controller.AgtmsController;
import net.saisimon.agtms.scanner.TemplateScanner;

@Configuration
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
