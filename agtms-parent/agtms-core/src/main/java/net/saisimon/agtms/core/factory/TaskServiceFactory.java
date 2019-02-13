package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.service.TaskService;

/**
 * 任务服务构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class TaskServiceFactory implements BeanPostProcessor {
	
	private static final List<TaskService> TASK_SERVICES = new ArrayList<>();
	
	public static TaskService get() {
		if (TASK_SERVICES.size() > 0) {
			return TASK_SERVICES.get(0);
		}
		throw new IllegalArgumentException("获取 TaskService 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof TaskService)) {
			return bean;
		}
		TaskService taskService = (TaskService) bean;
		TASK_SERVICES.add(taskService);
		TASK_SERVICES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
