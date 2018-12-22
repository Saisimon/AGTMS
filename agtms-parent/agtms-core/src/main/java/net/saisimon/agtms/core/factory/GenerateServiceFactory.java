package net.saisimon.agtms.core.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.StringUtils;

@Component
public class GenerateServiceFactory implements BeanPostProcessor {
	
	private static final Map<String, GenerateService> GENERATE_SERVICE_MAP = new ConcurrentHashMap<>(16);
	
	public static GenerateService build(Template template) {
		if (template == null || StringUtils.isBlank(template.getSource())) {
			throw new IllegalArgumentException("build GenerateService failed");
		}
		GenerateService generateService = GENERATE_SERVICE_MAP.get(template.getSource());
		if (generateService == null) {
			throw new IllegalArgumentException("build GenerateService failed");
		}
		generateService.init(template);
		return generateService;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof GenerateService)) {
			return bean;
		}
		GenerateService generateService = (GenerateService) bean;
		if (generateService.key() == null) {
			return bean;
		}
		GENERATE_SERVICE_MAP.put(generateService.key().getSource(), generateService);
		return bean;
	}
	
}
