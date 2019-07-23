package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.exception.AGTMSException;
import net.saisimon.agtms.core.service.SelectionService;

/**
 * 下拉列表服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class SelectionServiceFactory implements BeanPostProcessor {
	
	private static final List<SelectionService> SELECTION_SERVICES = new ArrayList<>();
	
	public static SelectionService get() {
		if (SELECTION_SERVICES.size() > 0) {
			return SELECTION_SERVICES.get(0);
		}
		throw new AGTMSException("获取 SelectionService 失败");
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (!(bean instanceof SelectionService)) {
			return bean;
		}
		SelectionService selectionService = (SelectionService) bean;
		SELECTION_SERVICES.add(selectionService);
		SELECTION_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
