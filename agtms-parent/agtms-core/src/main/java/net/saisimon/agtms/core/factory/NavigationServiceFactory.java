package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.exception.AGTMSException;
import net.saisimon.agtms.core.service.NavigationService;

/**
 * 导航服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class NavigationServiceFactory implements BeanPostProcessor {
	
	private static final List<NavigationService> NAVIGATION_SERVICES = new ArrayList<>();
	
	public static NavigationService get() {
		if (NAVIGATION_SERVICES.size() > 0) {
			return NAVIGATION_SERVICES.get(0);
		}
		throw new AGTMSException("获取 NavigationService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof NavigationService)) {
			return bean;
		}
		NavigationService navigationService = (NavigationService) bean;
		NAVIGATION_SERVICES.add(navigationService);
		NAVIGATION_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
