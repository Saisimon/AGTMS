package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.service.UserRoleService;

/**
 * 用户角色服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class UserRoleServiceFactory implements BeanPostProcessor {
	
	private static final List<UserRoleService> USER_ROLE_SERVICES = new ArrayList<>();
	
	public static UserRoleService get() {
		if (USER_ROLE_SERVICES.size() > 0) {
			return USER_ROLE_SERVICES.get(0);
		}
		throw new AgtmsException("获取 UserRoleService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof UserRoleService)) {
			return bean;
		}
		UserRoleService userRoleService = (UserRoleService) bean;
		USER_ROLE_SERVICES.add(userRoleService);
		USER_ROLE_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
