package net.saisimon.agtms.core.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.selection.Selection;

@Component
public class SelectionFactory implements BeanPostProcessor {
	
	private static final Map<String, Selection> SELECTION_MAP = new ConcurrentHashMap<>(16);
	
	public static Selection getSelection(String key) {
		if (key == null) {
			return null;
		}
		return SELECTION_MAP.get(key);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (!(bean instanceof AbstractSelection)) {
			return bean;
		}
		AbstractSelection selection = (AbstractSelection) bean;
		Selections key = selection.key();
		if (key == null) {
			return bean;
		}
		SELECTION_MAP.put(key.getName(), selection);
		return bean;
	}
	
}
