package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.cache.Cache;

/**
 * 缓存构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class CacheFactory implements BeanPostProcessor {
	
	private static final List<Cache> CACHES = new ArrayList<>();
	
	public static Cache get() {
		if (CACHES.size() > 0) {
			return CACHES.get(0);
		}
		throw new IllegalArgumentException("获取 Cache 失败");
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof Cache)) {
			return bean;
		}
		Cache cache = (Cache) bean;
		CACHES.add(cache);
		CACHES.sort(OrderComparator.INSTANCE);
		return bean;
	}
	
}
