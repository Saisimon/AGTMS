package net.saisimon.agtms.core.factory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.handler.OperationHandler;

/**
 * 操作记录处理器构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class OperationHandlerFactory implements BeanPostProcessor {
	
	private static final Map<Integer, OperationHandler> HANDLER_MAP = new HashMap<>(16);
	
	public static OperationHandler getHandler(OperateTypes operateType) {
		if (operateType == null) {
			return null;
		}
		return HANDLER_MAP.get(operateType.getType());
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof OperationHandler)) {
			return bean;
		}
		OperationHandler operationHandler = (OperationHandler) bean;
		if (operationHandler.keys() == null || operationHandler.keys().length == 0) {
			return bean;
		}
		for (OperateTypes operateType : operationHandler.keys()) {
			if (operateType == null) {
				continue;
			}
			HANDLER_MAP.put(operateType.getType(), operationHandler);
		}
		return bean;
	}
	
}
