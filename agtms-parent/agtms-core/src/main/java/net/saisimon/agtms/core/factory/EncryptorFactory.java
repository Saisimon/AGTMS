package net.saisimon.agtms.core.factory;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import cn.hutool.core.map.CaseInsensitiveMap;
import net.saisimon.agtms.core.encrypt.Encryptor;
import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 加密器构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class EncryptorFactory implements BeanPostProcessor {
	
	private static final Map<String, Encryptor> ENCRYPTOR_MAP = new CaseInsensitiveMap<>(16);
	
	public static Encryptor get(String algorithm) {
		if (SystemUtils.isBlank(algorithm)) {
			throw new AgtmsException("获取密码器失败");
		}
		return ENCRYPTOR_MAP.get(algorithm);
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof Encryptor)) {
			return bean;
		}
		Encryptor encryptor = (Encryptor) bean;
		if (SystemUtils.isBlank(encryptor.algorithm())) {
			return bean;
		}
		ENCRYPTOR_MAP.put(encryptor.algorithm(), encryptor);
		return bean;
	}
	
}
