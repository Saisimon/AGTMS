package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.service.UserService;

/**
 * 用户服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class UserServiceFactory implements BeanPostProcessor {
	
	private static final List<UserService> USER_SERVICES = new ArrayList<>();
	
	public static UserService get() {
		if (USER_SERVICES.size() > 0) {
			return USER_SERVICES.get(0);
		}
		throw new IllegalArgumentException("获取 UserService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof UserService)) {
			return bean;
		}
		UserService userService = (UserService) bean;
		USER_SERVICES.add(userService);
		USER_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
