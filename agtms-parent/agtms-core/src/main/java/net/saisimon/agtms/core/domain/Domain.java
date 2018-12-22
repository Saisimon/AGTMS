package net.saisimon.agtms.core.domain;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import net.saisimon.agtms.core.generate.DomainGenerater;

public interface Domain {
	
	default Object getField(String fieldName) {
		return DomainGenerater.getField(this, fieldName);
	}

	default void setField(String fieldName, Object fieldValue, Class<?> fieldClass) {
		DomainGenerater.setField(this, fieldName, fieldValue, fieldClass);
	}
	
	default Map<String, Object> toMap(Map<String, String> mapping) {
		if (!CollectionUtils.isEmpty(mapping)) {
			if (!mapping.containsKey("id")) {
				mapping.put("id", "id");
			}
			Map<String, Object> map = new HashMap<>();
			for (Entry<String, String> entry : mapping.entrySet()) {
				Field field = ReflectionUtils.findField(this.getClass(), entry.getKey());
				if (field != null) {
					field.setAccessible(true);
					map.put(entry.getValue(), ReflectionUtils.getField(field, this));
				}
			}
			return map;
		} else {
			return null;
		}
	}
	
}
