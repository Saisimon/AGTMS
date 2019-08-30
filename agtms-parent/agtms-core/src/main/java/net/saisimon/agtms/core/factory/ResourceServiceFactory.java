package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.service.ResourceService;

/**
 * 角色服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class ResourceServiceFactory implements BeanPostProcessor {
	
	private static final List<ResourceService> RESOURCE_SERVICES = new ArrayList<>();
	
	public static ResourceService get() {
		if (RESOURCE_SERVICES.size() > 0) {
			return RESOURCE_SERVICES.get(0);
		}
		throw new AgtmsException("获取 ResourceService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof ResourceService)) {
			return bean;
		}
		ResourceService roleService = (ResourceService) bean;
		RESOURCE_SERVICES.add(roleService);
		RESOURCE_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
