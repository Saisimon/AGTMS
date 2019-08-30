package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.service.RoleResourceService;

/**
 * 角色资源服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class RoleResourceServiceFactory implements BeanPostProcessor {
	
	private static final List<RoleResourceService> ROLE_RESOURCE_SERVICES = new ArrayList<>();
	
	public static RoleResourceService get() {
		if (ROLE_RESOURCE_SERVICES.size() > 0) {
			return ROLE_RESOURCE_SERVICES.get(0);
		}
		throw new AgtmsException("获取 RoleResourceService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof RoleResourceService)) {
			return bean;
		}
		RoleResourceService roleResourceService = (RoleResourceService) bean;
		ROLE_RESOURCE_SERVICES.add(roleResourceService);
		ROLE_RESOURCE_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
