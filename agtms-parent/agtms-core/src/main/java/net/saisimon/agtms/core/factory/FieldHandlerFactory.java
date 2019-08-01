package net.saisimon.agtms.core.factory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.handler.FieldHandler;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 属性字段处理器构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class FieldHandlerFactory implements BeanPostProcessor {
	
	private static final Map<String, FieldHandler> HANDLER_MAP = new HashMap<>(16);
	
	public static FieldHandler getHandler(String view) {
		if (SystemUtils.isBlank(view)) {
			throw new AgtmsException("获取 FieldHandler 失败");
		}
		return HANDLER_MAP.get(view);
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof FieldHandler)) {
			return bean;
		}
		FieldHandler fieldHandler = (FieldHandler) bean;
		Views view = fieldHandler.key();
		if (view == null) {
			return bean;
		}
		HANDLER_MAP.put(view.getView(), fieldHandler);
		return bean;
	}
	
}
