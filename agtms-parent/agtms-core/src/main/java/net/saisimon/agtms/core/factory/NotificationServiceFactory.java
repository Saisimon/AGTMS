package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.service.NotificationService;

/**
 * 消息通知服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class NotificationServiceFactory implements BeanPostProcessor {
	
	private static final List<NotificationService> NOTIFICATION_SERVICES = new ArrayList<>();
	
	public static NotificationService get() {
		if (NOTIFICATION_SERVICES.size() > 0) {
			return NOTIFICATION_SERVICES.get(0);
		}
		throw new AgtmsException("获取 NotificationService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof NotificationService)) {
			return bean;
		}
		NotificationService notificationService = (NotificationService) bean;
		NOTIFICATION_SERVICES.add(notificationService);
		NOTIFICATION_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
