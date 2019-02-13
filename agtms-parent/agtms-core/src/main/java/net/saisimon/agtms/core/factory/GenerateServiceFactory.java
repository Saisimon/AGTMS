package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.StringUtils;

/**
 * 自定义对象服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class GenerateServiceFactory implements BeanPostProcessor {
	
	private static final List<Sign> SIGNS = new ArrayList<>();
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
	
	public static List<Sign> getSigns() {
		return SIGNS;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof GenerateService)) {
			return bean;
		}
		GenerateService generateService = (GenerateService) bean;
		if (generateService.sign() == null || StringUtils.isBlank(generateService.sign().getName())) {
			return bean;
		}
		if (!SIGNS.contains(generateService.sign())) {
			SIGNS.add(generateService.sign());
			Collections.sort(SIGNS, (s1, s2) -> {
				return Integer.compare(s1.getOrder(), s1.getOrder());
			});
		}
		GENERATE_SERVICE_MAP.put(generateService.sign().getName(), generateService);
		return bean;
	}
	
}
