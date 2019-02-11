package net.saisimon.agtms.core.factory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import net.saisimon.agtms.core.handler.ParamHandler;

@Component
public class ParamHandlerFactory implements BeanPostProcessor {
	
	private static Map<Class<?>, ParamHandler<?>> handlerMap = new HashMap<>();

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ParamHandler) {
			Method method = ReflectionUtils.findMethod(bean.getClass(), "handler", Object.class);
			if (method != null) {
				handlerMap.put(method.getReturnType(), (ParamHandler<?>) bean);
			}
		}
		return bean;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ParamHandler<T> getHandler(Class<T> paramClass) {
		return (ParamHandler<T>) handlerMap.get(paramClass);
	}
	
}
