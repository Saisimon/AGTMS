package net.saisimon.agtms.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类
 * 
 * @author saisimon
 *
 */
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

	/**
	 * 获取上下文
	 * 
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return context;
	}

	/**
	 * 获取指定名称的 Spring bean 对象
	 * 
	 * @param name Spring bean 对象名称
	 * @return Spring bean 对象
	 */
	public static Object getBean(final String name) {
		return getBean(name, null);
	}

	/**
	 * 获取指定名称类型的 Spring bean 对象
	 * 
	 * @param name Spring bean 对象名称
	 * @param clazz Spring bean 对象类型
	 * @return Spring bean 对象
	 */
	public static <T> T getBean(final String name, final Class<T> clazz) {
		if (getApplicationContext() == null) {
			return null;
		} else {
			return getApplicationContext().getBean(name, clazz);
		}
	}
	
	/**
	 * 将指定对象装配为 Spring 管理的对象
	 * 
	 * @param obj 待装配对象 
	 * @return Spring 管理的对象
	 */
	public static <T> T autowire(T obj) {
		if (obj != null && getApplicationContext() != null) {
			getApplicationContext().getAutowireCapableBeanFactory().autowireBean(obj);
		}
		return obj;
	}
}
