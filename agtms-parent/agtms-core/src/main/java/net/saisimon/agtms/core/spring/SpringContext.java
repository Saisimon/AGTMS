package net.saisimon.agtms.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class SpringContext implements ApplicationContextAware {
	
	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (null == SpringContext.context) {
			SpringContext.context = applicationContext;
		}
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static Object getBean(final String name) {
		return getBean(name, null);
	}

	public static <T> T getBean(final String name, final Class<T> clazz) {
		if (getApplicationContext() == null) {
			return null;
		} else {
			return getApplicationContext().getBean(name, clazz);
		}
	}
	
	public static <T> T autowire(T obj) {
		if (obj != null && getApplicationContext() != null) {
			getApplicationContext().getAutowireCapableBeanFactory().autowireBean(obj);
		}
		return obj;
	}
}
