package net.saisimon.agtms.core.factory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.handler.FileHandler;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 文件处理器构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class FileHandlerFactory implements BeanPostProcessor {
	
	private static final Map<String, FileHandler> HANDLER_MAP = new HashMap<>(16);
	
	public static FileHandler getHandler(String type) {
		if (SystemUtils.isBlank(type)) {
			throw new AgtmsException("获取 FileHandler 失败");
		}
		return HANDLER_MAP.get(type);
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof FileHandler)) {
			return bean;
		}
		FileHandler fileHandler = (FileHandler) bean;
		FileTypes type = fileHandler.type();
		if (type == null) {
			return bean;
		}
		HANDLER_MAP.put(type.getType(), fileHandler);
		return bean;
	}
	
}
