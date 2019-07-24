package net.saisimon.agtms.core.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 执行器构建工厂
 * 
 * @author saisimon
 *
 */
@Component
public class ActuatorFactory implements BeanPostProcessor {

	private static final List<Sign> SIGNS = new ArrayList<>();
	private static final Map<String, Actuator<?>> ACTUATOR_MAP = new ConcurrentHashMap<>(16);
	
	public static Actuator<?> get(String key) {
		if (SystemUtils.isBlank(key)) {
			throw new AgtmsException("获取执行器失败");
		}
		return ACTUATOR_MAP.get(key);
	}
	
	public static List<Sign> getSigns() {
		return SIGNS;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (! (bean instanceof Actuator)) {
			return bean;
		}
		Actuator<?> actuator = (Actuator<?>) bean;
		if (actuator.sign() == null || SystemUtils.isBlank(actuator.sign().getName())) {
			return bean;
		}
		if (!SIGNS.contains(actuator.sign())) {
			SIGNS.add(actuator.sign());
			Collections.sort(SIGNS, (s1, s2) -> {
				return Integer.compare(s1.getOrder(), s1.getOrder());
			});
		}
		ACTUATOR_MAP.put(actuator.sign().getName(), actuator);
		return bean;
	}
	
}
