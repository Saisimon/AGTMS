package net.saisimon.agtms.core.factory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.service.ObjectStorageService;

/**
 * OSS 构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class ObjectStorageServiceFactory implements BeanPostProcessor {
	
	private static final Map<String, ObjectStorageService> OSS_MAP = new HashMap<>();
	
	public static ObjectStorageService get(String type) {
		return OSS_MAP.getOrDefault(type, OSS_MAP.get(ObjectStorageService.DEFAULT_TYPE));
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof ObjectStorageService)) {
			return bean;
		}
		ObjectStorageService oss = (ObjectStorageService) bean;
		if (oss.type() == null) {
			return bean;
		}
		OSS_MAP.put(oss.type(), oss);
		return bean;
	}
	
}
