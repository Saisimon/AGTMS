package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.service.OperationService;

/**
 * 操作记录服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class OperationServiceFactory implements BeanPostProcessor {
	
	private static final List<OperationService> OPERATION_SERVICES = new ArrayList<>();
	
	public static OperationService get() {
		if (OPERATION_SERVICES.size() > 0) {
			return OPERATION_SERVICES.get(0);
		}
		throw new IllegalArgumentException("获取 OperationService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof OperationService)) {
			return bean;
		}
		OperationService operationService = (OperationService) bean;
		OPERATION_SERVICES.add(operationService);
		OPERATION_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
