package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.service.RoleService;

/**
 * 角色服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class RoleServiceFactory implements BeanPostProcessor {
	
	private static final List<RoleService> ROLE_SERVICES = new ArrayList<>();
	
	public static RoleService get() {
		if (ROLE_SERVICES.size() > 0) {
			return ROLE_SERVICES.get(0);
		}
		throw new AgtmsException("获取 RoleService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof RoleService)) {
			return bean;
		}
		RoleService roleService = (RoleService) bean;
		ROLE_SERVICES.add(roleService);
		ROLE_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
