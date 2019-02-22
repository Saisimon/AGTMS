package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.service.TemplateService;

/**
 * 模板服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class TemplateServiceFactory implements BeanPostProcessor {
	
	private static final List<TemplateService> TEMPLATE_SERVICES = new ArrayList<>();
	
	public static TemplateService get() {
		if (TEMPLATE_SERVICES.size() > 0) {
			return TEMPLATE_SERVICES.get(0);
		}
		throw new IllegalArgumentException("获取 TemplateService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof TemplateService)) {
			return bean;
		}
		TemplateService templateService = (TemplateService) bean;
		TEMPLATE_SERVICES.add(templateService);
		TEMPLATE_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
